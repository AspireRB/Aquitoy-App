package com.aspire.aquitoy.Callback

import com.aspire.aquitoy.model.NurseGeoModel

interface FirebaseNurseInfoListener {
    fun onNurseInfoLoadSuccess(nurseGeoModel: NurseGeoModel?)
}