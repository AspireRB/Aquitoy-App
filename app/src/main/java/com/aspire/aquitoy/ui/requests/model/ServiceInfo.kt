package com.aspire.aquitoy.ui.requests.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceInfo(
    var serviceID: String = "", // Agrega la propiedad serviceID
    val nurseID: String = "",
    val sendHistory: String = "",
    val patientID: String = "",
    val patientLocationService: String = "",
    var state: String = "",
    val fecha: String = "",
    val patientName: String = "",
    val patientAge: String = "",
    val patientCedula: String = "",
    val patientFechaNacimiento: String = "",
    val nurseName: String = "",
    val nurseTarjeta: String = "",
    val medicalDiagnosis: String = "",
    val currentMedications: String = "",
    val medicalHistory: String = ""
) : Parcelable {

    // Constructor sin argumentos necesario para Firebase Database
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")

    // Para excluir la propiedad serviceID de la serialización a Firebase
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nurseID" to nurseID,
            "sendHistory" to sendHistory,
            "patientID" to patientID,
            "patientLocationService" to patientLocationService,
            "state" to state,
            "fecha" to fecha,
            "patientName" to patientName,
            "patientAge" to patientAge,
            "patientCedula" to patientCedula,
            "patientFechaNacimiento" to patientFechaNacimiento,
            "nurseName" to nurseName,
            "nurseTarjeta" to nurseTarjeta,
            "medicalDiagnosis" to medicalDiagnosis,
            "currentMedications" to currentMedications,
            "medicalHistory" to medicalHistory
        )
    }
}