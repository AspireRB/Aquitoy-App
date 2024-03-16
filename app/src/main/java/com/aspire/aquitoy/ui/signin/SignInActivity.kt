package com.aspire.aquitoy.ui.signin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, SignInActivity::class.java)
    }
}