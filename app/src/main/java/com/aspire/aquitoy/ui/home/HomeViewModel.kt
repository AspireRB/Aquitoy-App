package com.aspire.aquitoy.ui.home

import androidx.lifecycle.ViewModel
import com.aspire.aquitoy.data.DatabaseService
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val databaseService: DatabaseService) : ViewModel() {
    suspend fun initialServiceNurse(nurseID: String): LatLng? {
        val nurseLocation = databaseService.initialService(nurseID)
        return nurseLocation
    }

}