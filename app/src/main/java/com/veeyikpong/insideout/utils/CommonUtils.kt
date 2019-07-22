package com.veeyikpong.insideout.utils

import android.app.Activity
import android.content.Context
import android.location.Location
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.maps.model.LatLng
import com.veeyikpong.insideout.R

class CommonUtils {
    companion object{
        fun hideKeyboard(context: Activity){
            val view = context.window.currentFocus
            view?.let { v ->
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
            }
        }
    }
}