package com.veeyikpong.insideout.utils

import com.google.android.gms.maps.model.LatLng
import com.veeyikpong.insideout.model.Geofence

interface OnEditGeofenceListener {
    fun onEditSuccess(geofence: Geofence, deviceLocation: LatLng)
}