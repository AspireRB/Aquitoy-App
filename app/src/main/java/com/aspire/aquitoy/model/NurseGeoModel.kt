package com.aspire.aquitoy.model

import com.firebase.geofire.GeoLocation

class NurseGeoModel {
    var key: String?=null
    var geoLocation: GeoLocation?=null
    var nurseInfoModel: NurseInfoModel?=null

    constructor(key:String?,geoLocation: GeoLocation) {
        this.key = key
        this.geoLocation = geoLocation!!
    }
}