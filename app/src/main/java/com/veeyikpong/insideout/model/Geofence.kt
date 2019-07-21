package com.veeyikpong.insideout.model

import com.google.android.gms.maps.model.LatLng

class Geofence(var location: LatLng, var radius: Float = 0f, var wirelessNetworkName: String = "") {
}