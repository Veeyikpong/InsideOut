package com.veeyikpong.insideout.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

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