package com.aspire.aquitoy.ui.signin

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
import com.aspire.aquitoy.databinding.ActivitySignInBinding
import com.aspire.aquitoy.ui.FragmentsActivity
import com.aspire.aquitoy.ui.signin.model.UserSetInfo
import com.aspire.aquitoy.ui.signin.model.UserSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val signInViewModel: SignInViewModel by viewModels()

    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts
        .StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                signInViewModel.signInWithGoogle(account.idToken!!) { navigateToFragment() }
            } catch (e: ApiException) {
                Toast.makeText(this, "Ha ocurrido un error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
        binding.btnBack.setOnClickListener {
            navigateToIntroduction()
        }

        with(binding){
            btnCreateAccount.setOnClickListener {
                signInViewModel.register(
                    UserSignIn(
                        email = binding.etEmail.text.toString(),
                        password = binding.etPassword.text.toString()
                    ), UserSetInfo(
                        realName = binding.etRealName.text.toString(),
                        email = binding.etEmail.text.toString()
                    )
                ) { navigateToFragment() }
            }
        }

        binding.viewBottom.cardGoogle.setOnClickListener {
            signInViewModel.onGoogleLoginSelected {
                googleLauncher.launch(it.signInIntent)
            }
        }
    }

    private fun navigateToIntroduction() {
        finish()
    }

    private fun navigateToFragment() {
       startActivity(Intent(this, FragmentsActivity::class.java))
    }
}