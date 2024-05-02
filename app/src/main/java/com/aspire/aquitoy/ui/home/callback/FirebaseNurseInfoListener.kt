package com.aspire.aquitoy.ui.home.callback

import com.aspire.aquitoy.ui.home.model.NurseGeoModel

interface FirebaseNurseInfoListener {
    fun onNurseInfoLoadSuccess(nurseGeoModel: NurseGeoModel?)
}