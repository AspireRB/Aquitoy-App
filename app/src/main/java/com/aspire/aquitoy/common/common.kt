package com.aspire.aquitoy.common

import com.aspire.aquitoy.ui.home.model.NurseGeoModel
import com.google.android.gms.maps.model.Marker

object common {
    val markerList: MutableMap<String, Marker> = HashMap<String, Marker>()
    val nurseFound: MutableSet<NurseGeoModel> = HashSet<NurseGeoModel>()
    val PATIENT_INFO_REFERENCE: String = "/PatientInfo"
    val PATIENT_LOCATION_REFERENCE: String = "/PatientLocation"
    val NURSE_INFO_REFERENCES: String = "/NurseInfo"
    val NURSE_LOCATION_REFERENCES: String = "/NurseLocation"
}