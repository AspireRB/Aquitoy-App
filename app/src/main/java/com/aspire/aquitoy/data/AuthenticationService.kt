package com.aspire.aquitoy.data

import android.content.Context
import com.aspire.aquitoy.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
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
    suspend fun login(email:String, password:String): FirebaseUser? {
        return firebase.auth.signInWithEmailAndPassword(email, password).await().user
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
            firebase.auth.signInWithCredential(credential).addOnSuccessListener {
                cancellableContinuation.resume(it.user)
            }.addOnFailureListener {
                cancellableContinuation.resumeWithException(it)
            }
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