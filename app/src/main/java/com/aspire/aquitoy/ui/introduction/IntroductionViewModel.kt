package com.aspire.aquitoy.ui.introduction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aspire.aquitoy.core.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(): ViewModel() {
//    private val _navigateToLoginPatient = MutableLiveData<Event<Boolean>>()
//    val navigateToLoginPatient: LiveData<Event<Boolean>>
//        get() = _navigateToLoginPatient
//
//    private val _navigateToLoginNurse = MutableLiveData<Event<Boolean>>()
//    val navigateToLoginNurse: LiveData<Event<Boolean>>
//        get() = _navigateToLoginNurse
//
//    fun onLoginPatientSelected() {
//        _navigateToLoginPatient.value = Event(true)
//    }
//
//    fun onLoginNurseSelected() {
//        _navigateToLoginNurse.value = Event(true)
//    }
}