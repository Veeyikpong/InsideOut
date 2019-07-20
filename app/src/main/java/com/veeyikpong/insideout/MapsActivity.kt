package com.veeyikpong.insideout

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.location.Geofence
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mGeofencingClient: GeofencingClient
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mEditLocationBottomSheetFragment: EditLocationBottomSheetFragment
    private lateinit var mDeviceLocationMarker: Marker
    private lateinit var mGeofenceAreaCircle: Circle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        //setTransparentStatusBar()

        mGeofencingClient = LocationServices.getGeofencingClient(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
    }

    fun checkPermission() {
        //Permission handling
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                }
            })
            .check()
    }

    @SuppressLint("MissingPermission")
    fun initViews() {
        mEditLocationBottomSheetFragment = EditLocationBottomSheetFragment()
        mEditLocationBottomSheetFragment.setOnEditGeofenceListener(object : OnEditGeofenceListener {
            override fun onEditSuccess(geofence: com.veeyikpong.insideout.Geofence, deviceLocation: LatLng) {
                addGeofence(geofence)
                updateMarkerLocation(deviceLocation)
                checkInsideGeofence()
            }
        })

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.bottomFragmentContainer, mEditLocationBottomSheetFragment)
        fragmentTransaction.commit()

        mFusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                updateMarkerLocation(LatLng(location.latitude, location.longitude))
                mEditLocationBottomSheetFragment.setDeviceLocation(location.latitude, location.longitude)
            }.addOnFailureListener {
                updateMarkerLocation(AppConstants.KUALA_LUMPUR_LOCATION)
                mEditLocationBottomSheetFragment.setDeviceLocation(
                    AppConstants.KUALA_LUMPUR_LOCATION.latitude,
                    AppConstants.KUALA_LUMPUR_LOCATION.longitude
                )
            }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        initViews()
    }

    private fun updateMarkerLocation(location: LatLng) {
        // Add a marker in Sydney and move the camera
        if (::mDeviceLocationMarker.isInitialized) {
            mDeviceLocationMarker.remove()
        }
        val markerOptions = MarkerOptions().position(location).title(getString(R.string.device_location))
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        mDeviceLocationMarker = mMap.addMarker(markerOptions)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 18.0f))

        mDeviceLocationMarker.showInfoWindow()
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(geofence: com.veeyikpong.insideout.Geofence) {
        val mGeofence = buildGeofence(
            geofence.location.latitude, geofence.location.longitude, geofence.radius
        )

        if (geofence != null) {
            mGeofencingClient.addGeofences(
                buildGeofencingRequest(mGeofence!!), geofencePendingIntent
            ).addOnSuccessListener {
                val circleOptions = CircleOptions()
                    .center(geofence.location)
                    .radius(geofence.radius.toDouble())
                    .fillColor(
                        Color.argb(30, 30, 0, 255)
                    )
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(2F)

                if (::mGeofenceAreaCircle.isInitialized) {
                    mGeofenceAreaCircle.remove()
                }
                mGeofenceAreaCircle = mMap.addCircle(circleOptions)
            }.addOnFailureListener { e ->
                e.printStackTrace()
            }
        }
    }

    private fun buildGeofence(latitude: Double, longitude: Double, radius: Float): Geofence? {
        if (latitude != null && longitude != null && radius != null) {
            return Geofence.Builder()
                .setCircularRegion(
                    latitude,
                    longitude,
                    radius
                )
                .setRequestId("1")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }

        return null
    }


    private fun buildGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(0)
            .addGeofences(listOf(geofence))
            .build()
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofencingPendingIntent::class.java)
        PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun checkInsideGeofence() {
        if (!::mDeviceLocationMarker.isInitialized || !::mGeofenceAreaCircle.isInitialized) {
            return
        }
        val distance = FloatArray(2)

        Location.distanceBetween(
            mDeviceLocationMarker.position.latitude, mDeviceLocationMarker.position.longitude,
            mGeofenceAreaCircle.center.latitude, mGeofenceAreaCircle.center.longitude, distance
        )

        if (distance[0] > mGeofenceAreaCircle.radius) {
            tv_status.text = "Outside"
        } else {
            tv_status.text = "Inside"
        }
    }
}
