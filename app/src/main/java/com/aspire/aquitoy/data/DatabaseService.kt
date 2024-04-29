package com.aspire.aquitoy.data

import android.content.Context
import android.util.Log
import com.aspire.aquitoy.common.common
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class DatabaseService @Inject constructor(private val firebaseClient: FirebaseClient,
                                          @ApplicationContext private val context: Context) {
    suspend fun initialService(nurseID: String): LatLng? {
        return suspendCoroutine { continuation ->
            val nurseLocationRef = firebaseClient.db_rt.child(common.NURSE_LOCATION_REFERENCES)
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
}