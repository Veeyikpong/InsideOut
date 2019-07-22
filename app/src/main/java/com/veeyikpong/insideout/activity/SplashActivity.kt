package com.veeyikpong.insideout.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.veeyikpong.insideout.R
import com.veeyikpong.insideout.utils.AppConstants
import pub.devrel.easypermissions.EasyPermissions


class SplashActivity: AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if(!EasyPermissions.hasPermissions(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            EasyPermissions.requestPermissions(this, "",
                AppConstants.REQUEST_LOCATION_PERMISSION_SPLASH, Manifest.permission.ACCESS_FINE_LOCATION)

            return
        }

        postSplash(1000)
    }

    fun postSplash(delayMS: Long=0){
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        },delayMS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(requestCode == AppConstants.REQUEST_LOCATION_PERMISSION_SPLASH){
            postSplash()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if(requestCode == AppConstants.REQUEST_LOCATION_PERMISSION_SPLASH){
            postSplash()
        }
    }
}