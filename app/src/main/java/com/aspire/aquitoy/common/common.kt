package com.aspire.aquitoy.common

import com.google.android.gms.maps.model.Marker

object common {
    val markerList: MutableMap<String, Marker> = HashMap<String, Marker>()

    val PATIENT_INFO_REFERENCE: String = "PatientInfo"
    val PATIENT_LOCATION_REFERENCE: String = "PatientLocation"
    val NURSE_INFO_REFERENCES: String = "NurseInfo"
    val NURSE_LOCATION_REFERENCES: String = "NurseLocation/uid"
}