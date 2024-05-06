package com.aspire.aquitoy.data

import android.content.Context
import android.util.Log
import com.aspire.aquitoy.common.common
import com.aspire.aquitoy.ui.profile.model.UserInfo
import com.aspire.aquitoy.ui.requests.model.ServiceInfo
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class DatabaseService @Inject constructor(private val firebaseClient: FirebaseClient,
                                          @ApplicationContext private val context: Context) {

    private var state = true

    suspend fun getLocationNurse(nurseID: String): LatLng? {
        return suspendCoroutine { continuation ->
            val nurseLocationRef = firebaseClient.db_rt.child(common.NURSE_LOCATION_REFERENCE)
            nurseLocationRef.child(nurseID).get().addOnSuccessListener { dataSnapshot ->
                val locationArray = dataSnapshot.child("l").children.toList()
                if (locationArray.size >= 2) {
                    val latitude = locationArray[1].getValue(Double::class.java)
                    val longitude = locationArray[0].getValue(Double::class.java)
                    if (latitude != null && longitude != null) {
                        val location = LatLng(latitude, longitude)
                        Log.i("firebase", "Got location: $location")
                        continuation.resume(location)
                    } else {
                        Log.e("firebase", "Latitude or longitude is null")
                        continuation.resume(null)
                    }
                } else {
                    Log.e("firebase", "Invalid location array")
                    continuation.resume(null)
                }
            }.addOnFailureListener { exception ->
                Log.e("firebase", "Error getting data", exception)
                continuation.resumeWithException(exception)
            }
        }
    }

    fun insertToken(): Task<String> {
        return firebaseClient.messaging.token.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result
                if (token != null) {
                    val nurseInfoRef = firebaseClient.db_rt.child(common.PATIENT_INFO_REFERENCE)
                    nurseInfoRef.child("token").setValue(token)
                }
            }
        }
    }

    fun checkIdService(serviceId: String): Boolean {
        val serviceIdsRef = firebaseClient.db_rt.child(common.SERVICE_INFO_REFERENCE)
        var isUsed = false

        // Realizar consulta en la base de datos
        serviceIdsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val id = childSnapshot.getValue(String::class.java)
                    if (id == serviceId) {
                        isUsed = true
                        break
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("ServiceId", "Database Error")
            }
        })
        return isUsed
    }

    fun getService(): Query {
        val patientID = firebaseClient.auth.currentUser!!.uid
        val serviceInfoRef  = firebaseClient.db_rt.child(common.SERVICE_INFO_REFERENCE)
        val query = serviceInfoRef.orderByChild("patientID").equalTo(patientID)

        return query
    }

    fun updateState(asset : Boolean): Task<Void> {
        val patientID = firebaseClient.auth.currentUser!!.uid
        val patientInfoRef = firebaseClient.db_rt.child(common.PATIENT_INFO_REFERENCE)
        return patientInfoRef.child(patientID).child("state").setValue(asset).addOnFailureListener {
                exception ->
            Log.d("REALTIMEDATABASE", "ERROR: ${exception.message}")
        }
    }

    fun deleteService(serviceID: String?): Task<Void> {
        val serviceInfoRef = firebaseClient.db_rt.child(common.SERVICE_INFO_REFERENCE)
        val query = serviceInfoRef.child(serviceID!!).removeValue()

        return query
    }

    fun checkStateNurse(nurseID: String): Boolean {
        val nurseInfoRef = firebaseClient.db_rt.child(common.NURSE_INFO_REFERENCES)
        val stateNurse = nurseInfoRef.child(nurseID).child("state")

        stateNurse.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                val stateBD: Boolean = result?.getValue(Boolean::class.java) ?: false
                state = stateBD
            } else {
                Log.d("STATE", "NO OBTENIDO")
            }
        }

        return state
    }

    fun checkStatePatient(): Boolean {
        val patientID = firebaseClient.auth.currentUser!!.uid
        val patientInfoRef = firebaseClient.db_rt.child(common.PATIENT_INFO_REFERENCE)
        val statePatient = patientInfoRef.child(patientID).child("state")

        statePatient.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                val stateBD: Boolean = result?.getValue(Boolean::class.java) ?: false
                state = stateBD
            } else {
                Log.d("STATE", "NO OBTENIDO")
            }
        }

        return state
    }

    fun getAllServices(callback: (List<ServiceInfo>?, Throwable?) -> Unit) {
        val patientID = firebaseClient.auth.currentUser!!.uid
        val serviceInfoRef = firebaseClient.db_rt.child(common.SERVICE_INFO_REFERENCE)
        val query = serviceInfoRef.orderByChild("patientID").equalTo(patientID)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val serviceList = mutableListOf<ServiceInfo>()
                for (dataSnapshot in snapshot.children) {
                    val serviceInfo = dataSnapshot.getValue(ServiceInfo::class.java)
                    serviceInfo?.let {
                        it.serviceID = dataSnapshot.key ?: "" // Asigna el key como serviceID
                        serviceList.add(it)
                    }
                }
                callback(serviceList, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, error.toException())
            }
        })
    }

    fun getInfoUser(callback: (UserInfo?, Throwable?) -> Unit) {
        val currentUser = firebaseClient.auth.currentUser!!.uid
        val currentUserInfoRef = firebaseClient.db_rt.child(common.PATIENT_INFO_REFERENCE)
        currentUserInfoRef.child(currentUser).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                if (dataSnapshot != null && dataSnapshot.exists()) {
                    // Obtener los valores de la base de datos
                    val reaName = dataSnapshot.child("realName").getValue(String::class.java)
                    val email = dataSnapshot.child("email").getValue(String::class.java)
                    val rol = dataSnapshot.child("rol").getValue(String::class.java)
                    // Otros campos...

                    // Crear un objeto de modelo de datos con la información obtenida
                    val userInfo = UserInfo(reaName!!, email!!, rol!!)

                    // Devolver la información al llamador
                    callback(userInfo, null)
                } else {
                    // Manejar el caso donde los datos no existen
                    callback(null, Throwable("Data not found"))
                }
            } else {
                // Manejar el error
                callback(null, task.exception)
            }
        }
    }
}