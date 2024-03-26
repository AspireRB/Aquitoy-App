package com.aspire.aquitoy.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aspire.aquitoy.databinding.ActivityLoginPatientBinding
import com.aspire.aquitoy.ui.FragmentsActivity
import com.aspire.aquitoy.ui.signin.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
//    companion object {
//        fun create(context: Context): Intent =
//            Intent(context, LoginActivity::class.java)
//    }

    private lateinit var binding: ActivityLoginPatientBinding
    private val loginViewModel:LoginViewModel by viewModels()

    private val googleLauncher = registerForActivityResult(ActivityResultContracts
        .StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
             val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                loginViewModel.loginWithGoogle(account.idToken!!) { navigateToFragment() }
            } catch (e:ApiException) {
                Toast.makeText(this, "Ha ocurrido un error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
        binding.btnBack.setOnClickListener {
            navigateToIntroduction()
        }

        binding.btnLogin.setOnClickListener{
            loginViewModel.login(
            email = binding.etEmail.text.toString(),
            password = binding.etPassword.text.toString()
            ) { navigateToFragment() }
        }

        binding.tvFooter.setOnClickListener {
             navigateToSignIn()
        }

        binding.viewBottom.cardGoogle.setOnClickListener {
             loginViewModel.onGoogleLoginSelected {
                 googleLauncher.launch(it.signInIntent)
            }
        }
    }

    private fun navigateToIntroduction() {
        finish()
    }

    private fun navigateToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
    }

    private fun navigateToFragment() {
        startActivity(Intent(this, FragmentsActivity::class.java))
    }
}