package com.aspire.aquitoy.nurse.data

import com.aspire.aquitoy.nurse.ui.signin.model.UserSignIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor(private val firebase: com.aspire.aquitoy.nurse.data.FirebaseClient) {

    companion object {
        const val USER_COLLECTION = "users"
    }

    suspend fun createUserTable(userSignIn: UserSignIn) = runCatching {
        val id = firebase.auth.currentUser

        val user = hashMapOf(
            "id" to id!!.uid!!,
            "email" to userSignIn.email,
            "realname" to userSignIn.realName,
            "rol" to userSignIn.rol
        )

        firebase.db
            .collection(USER_COLLECTION)
            .document(id.uid).set(user)

    }.isSuccess
}