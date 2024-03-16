package com.aspire.aquitoy.ui.introduction

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import com.aspire.aquitoy.databinding.ActivityIntroductionBinding
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
        //startActivity(intent)
        //finish()

        initUI()
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        with(binding) {
            btnLoginPatient.setOnClickListener { introductionViewModel.onLoginPatientSelected() }
            btnLoginNurse.setOnClickListener { introductionViewModel.onLoginNurseSelected() }
        }
    }


    private fun initObservers() {
        introductionViewModel.navigateToLoginPatient.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToLoginPatient()
            }
        })
        introductionViewModel.navigateToLoginNurse.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToLoginNurse()
            }
        })
    }

    private fun goToLoginPatient() {
        startActivity(LoginActivity.create(this))
    }

    private fun goToLoginNurse() {
        startActivity(LoginActivity.create(this))
    }
}