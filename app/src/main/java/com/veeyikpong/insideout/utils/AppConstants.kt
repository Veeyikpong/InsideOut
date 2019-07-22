package com.veeyikpong.insideout.utils

import com.google.android.gms.maps.model.LatLng

class AppConstants {
    companion object{
        val KUALA_LUMPUR_LOCATION = LatLng(3.138675,101.616949)
        const val REQUEST_LOCATION_PERMISSION_SPLASH = 100
        const val REQUEST_USE_CURRENT_LOCATION = 200
        const val REQUEST_ADD_GEOFENCE = 300
    }
}