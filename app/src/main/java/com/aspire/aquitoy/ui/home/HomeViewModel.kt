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

    private val _serviceInfoLiveData = MutableLiveData<ServiceInfoModel>()
    val serviceInfoLiveData: LiveData<ServiceInfoModel> = _serviceInfoLiveData

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
                    // Print the data structure for debugging
                    Log.d("Service", "DataSnapshot: ${dataSnapshot.value}")

                    var serviceFound = false // Flag to track if a "create" service is found
                    for (snapshot in dataSnapshot.children) {
                        // Access service data based on actual data structure
                        val service = snapshot.getValue<Map<String, String>>() // Assuming the service data is a map

                        if (service != null && service["state"] == "accept") {
                            val serviceID = snapshot.key
                            val nurseID = service["nurseID"] ?: ""
                            val nurseLocationService = service["nurseLocationService"] ?: ""
                            val patientID = service["patientID"] ?: ""
                            val patientLocationService = service["patientLocationService"] ?: ""
                            val state = service["state"] ?: ""

                            val serviceInfoModel = ServiceInfoModel(
                                serviceID = serviceID,
                                nurseID = nurseID,
                                nurseLocationService = nurseLocationService,
                                patientID = patientID,
                                patientLocationService = patientLocationService,
                                state = state
                            )
                            _serviceInfoLiveData.value = serviceInfoModel
                            serviceFound = true
                            break
                        } else if (service != null && service["state"] == "decline") {
                            val serviceID = snapshot.key
                            val nurseID = service["nurseID"] ?: ""
                            val nurseLocationService = service["nurseLocationService"] ?: ""
                            val patientID = service["patientID"] ?: ""
                            val patientLocationService = service["patientLocationService"] ?: ""
                            val state = service["state"] ?: ""

                            val serviceInfoModel = ServiceInfoModel(
                                serviceID = serviceID,
                                nurseID = nurseID,
                                nurseLocationService = nurseLocationService,
                                patientID = patientID,
                                patientLocationService = patientLocationService,
                                state = state
                            )
                            _serviceInfoLiveData.value = serviceInfoModel
                            serviceFound = true
                            break
                        } else if (service != null && service["state"] == "finalized") {
                            val serviceID = snapshot.key
                            val nurseID = service["nurseID"] ?: ""
                            val nurseLocationService = service["nurseLocationService"] ?: ""
                            val patientID = service["patientID"] ?: ""
                            val patientLocationService = service["patientLocationService"] ?: ""
                            val state = service["state"] ?: ""

                            val serviceInfoModel = ServiceInfoModel(
                                serviceID = serviceID,
                                nurseID = nurseID,
                                nurseLocationService = nurseLocationService,
                                patientID = patientID,
                                patientLocationService = patientLocationService,
                                state = state
                            )
                            _serviceInfoLiveData.value = serviceInfoModel
                            serviceFound = true
                            break
                        }
                    }
                    if (!serviceFound) {
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
}