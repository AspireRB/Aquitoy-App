package com.aspire.aquitoy.ui.requests.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceInfo(
    var serviceID: String = "", // Agrega la propiedad serviceID
    val nurseID: String = "",
    val nurseLocationService: String = "",
    val patientID: String = "",
    val patientLocationService: String = "",
    var state: String = "",
    val patientName: String = "",
    val patientAge: String = "",
    val patientCedula: String = "",
    val fecha: String = "",
    val nurseName: String = "",
    val nurseCedula: String = "",
    val medicalHistory: String = "",
    val currentMedications: String = ""
) : Parcelable {

    // Constructor sin argumentos necesario para Firebase Database
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "", "", "")

    // Para excluir la propiedad serviceID de la serialización a Firebase
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nurseID" to nurseID,
            "nurseLocationService" to nurseLocationService,
            "patientID" to patientID,
            "patientLocationService" to patientLocationService,
            "state" to state,
            "patientName" to patientName,
            "patientAge" to patientAge,
            "patientCedula" to patientCedula,
            "fecha" to fecha,
            "nurseName" to nurseName,
            "nurseCedula" to nurseCedula,
            "medicalHistory" to medicalHistory,
            "currentMedications" to currentMedications
        )
    }
}