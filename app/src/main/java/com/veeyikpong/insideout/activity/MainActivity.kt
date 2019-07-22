package com.veeyikpong.insideout.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.veeyikpong.insideout.BuildConfig
import com.veeyikpong.insideout.R
import com.veeyikpong.insideout.fragment.SettingsFragment
import com.veeyikpong.insideout.utils.AppConstants
import com.veeyikpong.insideout.utils.CommonUtils
import com.veeyikpong.insideout.utils.GeofencingPendingIntent
import com.veeyikpong.insideout.utils.SetGeofenceListener
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mGeofencingClient: GeofencingClient
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mSettingsFragment: SettingsFragment
    private lateinit var mDeviceLocationMarker: Marker
    private lateinit var mGeofenceAreaCircle: Circle
    private lateinit var mCurrentGeofence: com.veeyikpong.insideout.model.Geofence

    private val distance = FloatArray(2)

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGeofencingClient = LocationServices.getGeofencingClient(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun initViews() {
        tv_about.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.GITHUB_PROJECT_URL))
            startActivity(browserIntent)
        }

        mSettingsFragment = SettingsFragment()
        mSettingsFragment.setListener(object : SetGeofenceListener {
            override fun onEditSuccess(geofence: com.veeyikpong.insideout.model.Geofence, deviceLocation: LatLng) {
                addGeofence(geofence)
                updateMarkerLocation(deviceLocation)
                checkInsideGeofence(deviceLocation, geofence)
                CommonUtils.hideKeyboard(this@MainActivity)
            }

            override fun onUseCurrentLocation() {
                useCurrentLocation()
            }
        })

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.bottomFragmentContainer, mSettingsFragment)
        fragmentTransaction.commitAllowingStateLoss()

        useCurrentLocation()
    }

    //call this function to use current location as device location
    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(AppConstants.REQUEST_USE_CURRENT_LOCATION)
    fun useCurrentLocation() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //If managed to get current location, default will be current location, else, will use Kuala Lumpur location as default
            mFusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    updateMarkerLocation(LatLng(location.latitude, location.longitude))
                    mSettingsFragment.setDeviceLocation(location.latitude, location.longitude)
                    Toasty.info(this@MainActivity, getString(R.string.use_current_location), Toast.LENGTH_SHORT, true)
                        .show()
                }.addOnFailureListener {
                    updateMarkerLocation(AppConstants.KUALA_LUMPUR_LOCATION)
                    mSettingsFragment.setDeviceLocation(
                        AppConstants.KUALA_LUMPUR_LOCATION.latitude,
                        AppConstants.KUALA_LUMPUR_LOCATION.longitude
                    )
                    Toasty.info(
                        this@MainActivity,
                        getString(R.string.failed_to_get_current_location),
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
        } else {
            EasyPermissions.requestPermissions(
                this@MainActivity, getString(R.string.permission_rationale_current_location),
                AppConstants.REQUEST_USE_CURRENT_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        initViews()
    }

    //Update marker location on map, and animate camera to that location
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

    //Add geofence area on google map
    @AfterPermissionGranted(AppConstants.REQUEST_ADD_GEOFENCE)
    fun addGeofence(geofence: com.veeyikpong.insideout.model.Geofence) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mCurrentGeofence = geofence
            val mGeofence = buildGeofence(
                geofence.location.latitude, geofence.location.longitude, geofence.radius
            )

            if (mGeofence != null) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }

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
        } else {
            EasyPermissions.requestPermissions(
                this@MainActivity, getString(R.string.permission_rationale_geofence),
                AppConstants.REQUEST_ADD_GEOFENCE, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    //Build geofence variable
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

    //Check if device location is inside geofence area
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
                    if (wifiInfo.ssid.replace("\"", "").equals(geofence.wirelessNetworkName, true)) {
                        setInside(getString(R.string.wifi_network))
                        return
                    }
                }
            }
        }

        //Geographical location check
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

    //Show device is inside geofence area
    private fun setInside(determineFactor: String = getString(R.string.geographical_location)) {
        tv_result.text = getString(R.string.inside)
        tv_determine_factor.text = determineFactor
        ll_determine_factor.visibility = View.VISIBLE
        tv_result.setTextColor(ContextCompat.getColor(this, R.color.successGreen))
        Toasty.success(this, getString(R.string.device_inside_message), Toast.LENGTH_SHORT, true).show()
    }

    //Show device is outside geofence area
    private fun setOutside() {
        tv_result.text = getString(R.string.outside)
        ll_determine_factor.visibility = View.GONE
        tv_result.setTextColor(Color.RED)
        Toasty.error(this, getString(R.string.device_outside_message), Toast.LENGTH_SHORT, true).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}
