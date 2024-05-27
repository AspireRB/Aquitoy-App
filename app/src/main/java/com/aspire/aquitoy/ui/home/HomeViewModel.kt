package com.aspire.aquitoy.ui.home

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aspire.aquitoy.common.common
import com.aspire.aquitoy.data.DatabaseService
import com.aspire.aquitoy.data.FirebaseClient
import com.aspire.aquitoy.ui.home.model.ServiceInfo
import com.aspire.aquitoy.ui.home.model.ServiceInfoModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val databaseService: DatabaseService, private
val firebaseClient: FirebaseClient, private val serviceInfo: ServiceInfo, private val context: Context) :
    ViewModel() {

    private val _serviceInfoListLiveData = MutableLiveData<List<ServiceInfoModel>>()
    val serviceInfoListLiveData: LiveData<List<ServiceInfoModel>> = _serviceInfoListLiveData

    fun getLocationNurse(nurseID: String, callback: (LatLng?) -> Unit) {
        databaseService.getLocationNurse(nurseID) { location ->
            if (location != null) {
                Log.i("HomeViewModel", "Nurse location: $location")
                callback(location)
            } else {
                Log.e("HomeViewModel", "Failed to get nurse location")
                callback(null)
            }
        }
    }

    fun initialService(nurseID: String, coordinates: LatLng) {
        val serviceId = generateUniqueServiceId()
        val idPatient = firebaseClient.auth.currentUser?.uid.toString()

        val patientLatitude = coordinates.latitude
        val patientLongitude = coordinates.longitude

        val updatedServiceInfo = serviceInfo.copy(
            nurseID = nurseID,
            patientID = idPatient,
            patientLocationService = "$patientLatitude,$patientLongitude"
        )

        val serviceInfoRef = firebaseClient.db_rt.child(common.SERVICE_INFO_REFERENCE)
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

    fun getService() {
        val serviceReference = databaseService.getService()
        serviceReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("Service", "DataSnapshot: ${dataSnapshot.value}")

                    val serviceList = mutableListOf<ServiceInfoModel>()

                    for (snapshot in dataSnapshot.children) {
                        val service = snapshot.getValue<Map<String, String>>()
                        val serviceID = snapshot.key
                        val nurseID = service?.get("nurseID") ?: ""
                        val patientID = service?.get("patientID") ?: ""
                        val patientLocationService = service?.get("patientLocationService") ?: ""
                        val state = service?.get("state") ?: ""

                        if (service != null) {
                            val serviceInfoModel = ServiceInfoModel(
                                serviceID = serviceID,
                                nurseID = nurseID,
                                patientID = patientID,
                                patientLocationService = patientLocationService,
                                state = state
                            )
                            serviceList.add(serviceInfoModel)
                        }
                    }

                    if (serviceList.isNotEmpty()) {
                        _serviceInfoListLiveData.value = serviceList
                    } else {
                        Log.d("Service", "No service found")
                    }
                } else {
                    Log.d("Service", "No service data found")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("RealtimeDatabase", "${databaseError.message}")
            }
        })
    }

    fun deleteService(serviceID: String?) {
        databaseService.deleteService(serviceID).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Servicio rechazado", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("SERVICIO", "NO SE PUDO ELIMINAR")
            }
        }
    }

    fun checkState(nurseID: String): Boolean = runBlocking {
        val stateNurseDeferred = async { databaseService.checkStateNurse(nurseID) }
        val statePatientDeferred = async { databaseService.checkStatePatient() }

        val stateNurse = stateNurseDeferred.await()
        val statePatient = statePatientDeferred.await()

        Log.d("STATE", "${stateNurse && statePatient}")
        return@runBlocking stateNurse && statePatient
    }

    fun updateState(state: Boolean) {
        databaseService.updateState(state).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("STATE", "ACTUALIZADO")
            } else {
                Log.d("STATE", "FALLO ACTUALIZAR STATE")
            }
        }
    }

    fun getState(): Boolean = runBlocking {
        val statePatientDeferred = async { databaseService.getState() }
        val statePatient = statePatientDeferred.await()

        Log.d("STATE", "${statePatient}")
        return@runBlocking statePatient
    }
}