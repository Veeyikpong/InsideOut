package com.veeyikpong.insideout.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.wifi.WifiInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import kotlinx.android.synthetic.main.activity_main.*
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.view.View
import android.widget.Toast
import com.veeyikpong.insideout.utils.GeofencingPendingIntent
import com.veeyikpong.insideout.R
import com.veeyikpong.insideout.fragment.SettingsFragment
import com.veeyikpong.insideout.utils.AppConstants
import com.veeyikpong.insideout.utils.CommonUtils
import com.veeyikpong.insideout.utils.OnEditGeofenceListener
import es.dmoral.toasty.Toasty


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mGeofencingClient: GeofencingClient
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mSettingsFragment: SettingsFragment
    private lateinit var mDeviceLocationMarker: Marker
    private lateinit var mGeofenceAreaCircle: Circle

    private val distance = FloatArray(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        mSettingsFragment = SettingsFragment()
        mSettingsFragment.setOnEditGeofenceListener(object : OnEditGeofenceListener {
            override fun onEditSuccess(geofence: com.veeyikpong.insideout.model.Geofence, deviceLocation: LatLng) {
                addGeofence(geofence)
                updateMarkerLocation(deviceLocation)
                checkInsideGeofence(deviceLocation, geofence)
                CommonUtils.hideKeyboard(this@MainActivity)
            }
        })

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.bottomFragmentContainer, mSettingsFragment)
        fragmentTransaction.commit()

        //If managed to get current location, default will be current location, else, will use Kuala Lumpur location as default
        mFusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                updateMarkerLocation(LatLng(location.latitude, location.longitude))
                mSettingsFragment.setDeviceLocation(location.latitude, location.longitude)
            }.addOnFailureListener {
                updateMarkerLocation(AppConstants.KUALA_LUMPUR_LOCATION)
                mSettingsFragment.setDeviceLocation(
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
    fun addGeofence(geofence: com.veeyikpong.insideout.model.Geofence) {
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

    private fun checkInsideGeofence(deviceLocation: LatLng, geofence: com.veeyikpong.insideout.model.Geofence) {
        //prioritize Wifi Network
        if (geofence.wirelessNetworkName.isNotEmpty()) {
            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo

            wifiInfo = wifiManager.connectionInfo
            if (wifiInfo != null && wifiInfo.supplicantState == SupplicantState.COMPLETED) {
                if (wifiInfo.ssid != null) {
                    //If wifi network matched, no need to check geographical location
                    //ssid will return double quote or backslash together, remove them
                    if (wifiInfo.ssid.replace("\"","").equals(geofence.wirelessNetworkName)) {
                        setInside(getString(R.string.wifi_network))
                        return
                    }
                }
            }
        }

        Location.distanceBetween(
            deviceLocation.latitude, deviceLocation.longitude,
            geofence.location.latitude, geofence.location.longitude, distance
        )

        //if distance more than radius (outside circle), means outside, else the location is inside the geofence area
        if (distance[0] > geofence.radius) {
            setOutside()
        } else {
            setInside()
        }
    }

    private fun setInside(determineFactor: String = getString(R.string.geographical_location)) {
        tv_result.text = getString(R.string.inside)
        tv_determine_factor.text = determineFactor
        ll_determine_factor.visibility = View.VISIBLE
        tv_result.setTextColor(ContextCompat.getColor(this, R.color.successGreen))
        Toasty.success(this, getString(R.string.device_inside_message), Toast.LENGTH_SHORT, true).show();
    }

    private fun setOutside(determineFactor: String = getString(R.string.geographical_location)) {
        tv_result.text = getString(R.string.outside)
        tv_determine_factor.text = determineFactor
        ll_determine_factor.visibility = View.VISIBLE
        tv_result.setTextColor(Color.RED)
        Toasty.error(this, getString(R.string.device_outside_message), Toast.LENGTH_SHORT, true).show();
    }
}
