package com.aspire.aquitoy.ui.signin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aspire.aquitoy.data.AuthenticationService
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val authenticationService: AuthenticationService): ViewModel() {
    private var _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun register(email: String, password: String, navigateToFragment: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                 val result = withContext(Dispatchers.IO) {
                    authenticationService.register(email, password)
                }

                if(result != null) {
                    navigateToFragment()
                } else {
                    Log.i("aspire", "error!!")
                }

            } catch (e: Exception) {
                Log.i("aspire", e.message.orEmpty())
            }

            _isLoading.value = false
        }
    }

    fun onGoogleLoginSelected(googleLauncherLogin: (GoogleSignInClient) -> Unit) {
        val gsc = authenticationService.getGoogleClient()
        googleLauncherLogin(gsc)
    }

    fun signInWithGoogle(idToken: String, navigateToFragment: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authenticationService.loginWithGoogle(idToken)
            }
            if (result != null) {
                navigateToFragment()
            }
        }
    }
}