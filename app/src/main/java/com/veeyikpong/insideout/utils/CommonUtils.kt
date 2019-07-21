package com.veeyikpong.insideout.utils

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.maps.model.BitmapDescriptor

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