package com.aspire.aquitoy.ui.signin

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aspire.aquitoy.databinding.ActivitySignInBinding
import com.aspire.aquitoy.ui.FragmentsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
//    companion object {
//        fun create(context: Context): Intent =
//            Intent(context, SignInActivity::class.java)
//    }

    private lateinit var binding: ActivitySignInBinding
    private val signInViewModel:SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
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
                signInViewModel.isLoading.collect {
                    binding.pbLoading.isVisible = it
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnCreateAccount.setOnClickListener { signInViewModel.register(
            email = binding.etEmail.text.toString(),
            password = binding.etPassword.text.toString()
        ) { navigateToFragment() } }
    }

    private fun navigateToFragment() {
       startActivity(Intent(this, FragmentsActivity::class.java))
    }
}