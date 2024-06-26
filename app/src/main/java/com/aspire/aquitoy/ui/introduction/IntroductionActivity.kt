package com.aspire.aquitoy.ui.introduction

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aspire.aquitoy.databinding.ActivityIntroductionBinding
import com.aspire.aquitoy.ui.FragmentsActivity
import com.aspire.aquitoy.ui.login.LoginActivity
import com.aspire.aquitoy.ui.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroductionBinding
    private val introductionViewModel: IntroductionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityIntroductionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread.sleep(1000)
        splashScreen.setKeepOnScreenCondition { false }

        when(introductionViewModel.checkDestination()) {
            IntruductionDestination.Home -> navigateToHome()
            IntruductionDestination.Nothing -> navigateToNothing()
            else -> {}
        }

        val buttonLogin = binding.btnLogin
        val buttonSignIn = binding.btnSignIn

        buttonLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        buttonSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun navigateToNothing(){ }

    private fun navigateToHome(){
        startActivity(Intent(this, FragmentsActivity::class.java))
    }
}