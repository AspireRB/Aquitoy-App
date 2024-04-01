package com.aspire.aquitoy.ui.signin.model

import com.aspire.aquitoy.data.AuthenticationService

private lateinit var authenticationService: AuthenticationService

data class UserSignIn(
    val realName: String,
    val email: String,
    val password: String,
    val rol: String = "patient"
){
    val id: String
        get() = authenticationService.getId()
}