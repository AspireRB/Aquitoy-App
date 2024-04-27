package com.aspire.aquitoy.common

import com.aspire.aquitoy.model.NurseGeoModel
import com.google.android.gms.maps.model.Marker

object common {
    val markerList: MutableMap<String, Marker> = HashMap<String, Marker>()
    val nurseFound: MutableSet<NurseGeoModel> = HashSet<NurseGeoModel>()
    val NURSE_LOCATION_REFERENCES: String = "NurseLocation"
    val PATIENT_LOCATION_REFERENCE: String = "PatientLocation"
}