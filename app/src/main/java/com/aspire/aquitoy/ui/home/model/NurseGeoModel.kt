package com.aspire.aquitoy.ui.home.model

import com.firebase.geofire.GeoLocation

class NurseGeoModel {
    var key: String?=null
    var geoLocation: GeoLocation?=null

    constructor(key:String?,geoLocation: GeoLocation) {
        this.key = key
        this.geoLocation = geoLocation!!
    }
}