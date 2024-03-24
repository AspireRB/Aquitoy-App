package com.aspire.aquitoy.ui.introduction

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
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
//        startActivity(intent)
//        finish()
//
//        initUI()
        when(introductionViewModel.checkDestination()) {
            IntruductionDestination.Home -> navigateToHome()
            IntruductionDestination.Login -> navigateToLogin()
        }

        val buttonPatient = binding.btnLoginPatient
        val buttonNurse = binding.btnLoginNurse

        buttonPatient.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        buttonNurse.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun navigateToLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun navigateToHome(){
        startActivity(Intent(this, FragmentsActivity::class.java))
    }

//    private fun initUI() {
//        initListeners()
//        initObservers()
//    }
//
//    private fun initListeners() {
//        with(binding) {
//            btnLoginPatient.setOnClickListener { introductionViewModel.onLoginPatientSelected() }
//            btnLoginNurse.setOnClickListener { introductionViewModel.onLoginNurseSelected() }
//        }
//    }
//
//    private fun initObservers() {
//        introductionViewModel.navigateToLoginPatient.observe(this, Observer {
//            it.getContentIfNotHandled()?.let {
//                goToLoginPatient()
//            }
//        })
//        introductionViewModel.navigateToLoginNurse.observe(this, Observer {
//            it.getContentIfNotHandled()?.let {
//                goToLoginNurse()
//            }
//        })
//    }
//
//    private fun goToLoginPatient() {
//        startActivity(Intent(this, LoginActivity::class.java))
//    }
//
//    private fun goToLoginNurse() {
//        startActivity(Intent(this,SignInActivity::class.java))
//    }
}