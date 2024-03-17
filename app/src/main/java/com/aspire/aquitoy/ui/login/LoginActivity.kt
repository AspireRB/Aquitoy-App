package com.aspire.aquitoy.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aspire.aquitoy.databinding.ActivityLoginPatientBinding
import com.aspire.aquitoy.ui.FragmentsActivity
import com.aspire.aquitoy.ui.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
//    companion object {
//        fun create(context: Context): Intent =
//            Intent(context, LoginActivity::class.java)
//
//    }

    private lateinit var binding: ActivityLoginPatientBinding
    private val loginViewModel:LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initUIState()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.isLoading.collect {
                     binding.pbLoading.isVisible = it
                }
            }
        }
    }

    private fun initListeners() {
       binding.btnLogin.setOnClickListener{
           loginViewModel.login(
           email = binding.etEmail.text.toString(),
           password = binding.etPassword.text.toString()
           ) { navigateToFragment() }
       }

       binding.viewBottom.tvFooter.setOnClickListener {
            navigateToSignIn()
       }
    }

    private fun navigateToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
    }

    private fun navigateToFragment() {
        startActivity(Intent(this, FragmentsActivity::class.java))
    }
}