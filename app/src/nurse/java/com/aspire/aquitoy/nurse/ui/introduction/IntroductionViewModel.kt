package com.aspire.aquitoy.nurse.ui.introduction

import androidx.lifecycle.ViewModel
import com.aspire.aquitoy.data.AuthenticationService
import com.aspire.aquitoy.ui.introduction.IntruductionDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(private val authenticationService: AuthenticationService): ViewModel() {

    private fun isUserLogged():Boolean {
        return authenticationService.isUserLogged()
    }
    fun checkDestination(): IntruductionDestination {
        val isUserLogged = isUserLogged()
        return if (isUserLogged) {
            IntruductionDestination.Home
        } else {
            IntruductionDestination.Nothing
        }
    }
}

sealed class IntruductionDestination {
    object Nothing: IntruductionDestination()
    object Home: IntruductionDestination()
}