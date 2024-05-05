package com.aspire.aquitoy.ui.home.model

data class ServiceInfo(
    val nurseID: String?=null,
    val nurseLocationService: String?=null,
    val patientID: String?=null,
    val patientLocationService: String?=null,
    val state: String = "create"
)