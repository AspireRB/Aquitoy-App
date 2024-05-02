package com.aspire.aquitoy.ui.signin

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aspire.aquitoy.common.common
import com.aspire.aquitoy.data.AuthenticationService
import com.aspire.aquitoy.data.FirebaseClient
import com.aspire.aquitoy.ui.signin.model.UserSetInfo
import com.aspire.aquitoy.ui.signin.model.UserSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("DEPRECATION")
@HiltViewModel
class SignInViewModel @Inject constructor(private val authenticationService:
                                          AuthenticationService, private val firebaseClient:
FirebaseClient, private val context: Context
): ViewModel() {
    private var _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    val patientInfoRef = firebaseClient.db_rt.child(common.PATIENT_INFO_REFERENCE)

    fun register(userSignIn: UserSignIn, userSetInfo: UserSetInfo, navigateToFragment: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                 val result = withContext(Dispatchers.IO) {
                    authenticationService.register(userSignIn.email, userSignIn.password)
                }
                if(result != null) {
                    patientInfoRef.child(firebaseClient.auth.currentUser!!.uid).setValue(userSetInfo)
                    navigateToFragment()
                    showToast("Ingreso exitoso")
                } else {
                    showToast("Hubo algun error")
                    Log.i("aspire", "error!!")
                }
            } catch (e: Exception) {
                showToast("Email ya registrado")
                Log.i("aspire", e.message.orEmpty())
            }
            _isLoading.value = false
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
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
                showToast("Ingreso exitoso")
            }
        }
    }
}