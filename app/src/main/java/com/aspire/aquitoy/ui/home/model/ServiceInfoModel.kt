package com.aspire.aquitoy.ui.home.model

data class ServiceInfoModel(
    val serviceID: String?=null,
    val nurseID: String?=null,
    val nurseLocationService: String?=null,
    val patientID: String?=null,
    val patientLocationService: String?=null,
    var state: String?=null
)