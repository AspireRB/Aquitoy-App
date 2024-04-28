package com.aspire.aquitoy.ui.signin.model

data class UserSignIn(
    val realName: String,
    val email: String,
    val password: String,
    val rol: String = "patient"
)