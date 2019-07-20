package com.veeyikpong.insideout

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import android.graphics.drawable.Drawable
import android.R
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import com.google.android.gms.maps.model.BitmapDescriptor

class CommonUtils {
    companion object{
        fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
            val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
            vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
            val bitmap = Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

        fun hideKeyboard(context: Activity){
            val view = context.window.currentFocus
            view?.let { v ->
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
            }
        }
    }
}