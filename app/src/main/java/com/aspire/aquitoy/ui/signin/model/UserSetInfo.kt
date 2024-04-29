package com.aspire.aquitoy.ui.signin.model

data class UserSetInfo(
    val realName: String,
    val email: String,
    val rol: String = "patient"
)