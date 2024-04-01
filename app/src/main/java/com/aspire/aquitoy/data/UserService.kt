package com.aspire.aquitoy.data

import com.aspire.aquitoy.ui.signin.model.UserSignIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor(private val firebase: FirebaseClient) {

    companion object {
        const val USER_COLLECTION = "users"
    }

    suspend fun createUserTable(userSignIn: UserSignIn) = runCatching {

        val user = hashMapOf(
//            "id" to userSignIn.id,
            "email" to userSignIn.email,
            "realname" to userSignIn.realName,
            "rol" to userSignIn.rol
        )

        firebase.db
            .collection(USER_COLLECTION)
            .add(user).await()

    }.isSuccess
}