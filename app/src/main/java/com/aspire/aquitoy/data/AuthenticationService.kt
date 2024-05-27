package com.aspire.aquitoy.data

import android.content.Context
import com.aspire.aquitoy.R
import com.aspire.aquitoy.common.common
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("DEPRECATION")
@Singleton
class AuthenticationService @Inject constructor(private val firebase: FirebaseClient,
                                                @ApplicationContext private val context: Context) {
    suspend fun login(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = firebase.auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                val role = checkUserRole(user.uid)
                if (role == "patient") {
                    user
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    private suspend fun checkUserRole(uid: String): String? {
        return suspendCancellableCoroutine { continuation ->
            val userRef = firebase.db_rt.child(common.PATIENT_INFO_REFERENCE).child(uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val role = snapshot.child("rol").getValue(String::class.java)
                    continuation.resume(role)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(null)
                }
            })
        }
    }

    suspend fun register(email: String, password: String): FirebaseUser? {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    cancellableContinuation.resume(it.user)
                }
                .addOnFailureListener {
                    cancellableContinuation.resumeWithException(it)
                }
        }
    }

    fun isUserLogged(): Boolean {
        return getCurrentuser() != null
    }

    fun logout() {
        firebase.auth.signOut()
    }

    private fun getCurrentuser() = firebase.auth.currentUser

    private suspend fun completeRegisterWithCredential(credential: AuthCredential): FirebaseUser? {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebase.auth.signInWithCredential(credential).addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                // Guardar el nombre y correo electrónico en la base de datos
                saveGoogleUserDataToDatabase(firebaseUser)
                cancellableContinuation.resume(firebaseUser)
            }.addOnFailureListener {
                cancellableContinuation.resumeWithException(it)
            }
        }
    }

    private fun saveGoogleUserDataToDatabase(user: FirebaseUser?) {
        user?.let { firebaseUser ->
            val uid = firebaseUser.uid
            val name = firebaseUser.displayName
            val email = firebaseUser.email
            val rol = "patient"
            val state = true
            // Guardar el nombre y correo electrónico en la base de datos, por ejemplo:
            val userRef = firebase.db_rt.child(common.PATIENT_INFO_REFERENCE).child(uid)
            val userMap = HashMap<String, Any>()
            userMap["email"] = email ?: ""
            userMap["realName"] = name ?: ""
            userMap["rol"] = rol
            userMap["state"] = state

            userRef.updateChildren(userMap)
        }
    }

    fun getGoogleClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)).requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun loginWithGoogle(idToken: String): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return completeRegisterWithCredential(credential)
    }
}