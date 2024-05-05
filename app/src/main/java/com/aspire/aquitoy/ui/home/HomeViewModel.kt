package com.aspire.aquitoy.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aspire.aquitoy.common.common
import com.aspire.aquitoy.data.DatabaseService
import com.aspire.aquitoy.data.FirebaseClient
import com.aspire.aquitoy.ui.home.model.ServiceInfo
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val databaseService: DatabaseService, private
val firebaseClient: FirebaseClient, private val serviceInfo: ServiceInfo) :
    ViewModel() {
    suspend fun getLocationNurse(nurseID: String): LatLng? {
        val nurseLocation = databaseService.getLocationNurse(nurseID)
        return nurseLocation
    }

    fun createToken() {
        val result = databaseService.insertToken()
        if (result.isComplete){
            Log.d("Token", "Agregado")
        } else {
            Log.d("Token", "Fallo")
        }
    }

    fun initialService(nurseID: String, nurseLocationService: LatLng?, coordinates: LatLng) {
        val serviceId = generateUniqueServiceId()
        val idPatient = firebaseClient.auth.currentUser?.uid.toString()


        val nurseLatitude = nurseLocationService?.latitude ?: 0.0
        val nurseLongitude = nurseLocationService?.longitude ?: 0.0
        val patientLatitude = coordinates.latitude
        val patientLongitude = coordinates.longitude

        val updatedServiceInfo = serviceInfo.copy(
            nurseID = nurseID,
            nurseLocationService = "$nurseLatitude,$nurseLongitude",
            patientID = idPatient,
            patientLocationService = "$patientLatitude,$patientLongitude"
        )

        val serviceInfoRef = firebaseClient.db_rt.child(common.SERVICE_INFO_REFERENCES)
        serviceInfoRef.child(serviceId).setValue(updatedServiceInfo)
    }

    fun generateUniqueServiceId(): String {
        var randomServiceId: String = ""
        randomServiceId = generateRandomId()
        return randomServiceId
    }

    private fun generateRandomId(): String {
        return "SERVICE_${(0..9999).random()}"
    }
}