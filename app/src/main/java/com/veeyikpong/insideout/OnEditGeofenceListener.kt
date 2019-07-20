package com.veeyikpong.insideout

import com.google.android.gms.maps.model.LatLng

interface OnEditGeofenceListener {
    fun onEditSuccess(geofence: Geofence, deviceLocation: LatLng)
}