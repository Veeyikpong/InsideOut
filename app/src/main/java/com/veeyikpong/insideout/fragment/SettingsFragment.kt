package com.veeyikpong.insideout.fragment


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.veeyikpong.insideout.R
import com.veeyikpong.insideout.model.Geofence
import com.veeyikpong.insideout.utils.CommonUtils
import com.veeyikpong.insideout.utils.SetGeofenceListener
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment() : Fragment() {

    private lateinit var setGeofenceListener: SetGeofenceListener

    fun setListener(listener: SetGeofenceListener){
        this.setGeofenceListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et_device_latitude.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                et_latitude.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        et_device_longitude.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                et_longitude.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        et_latitude.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                et_latitude.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        et_longitude.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                et_longitude.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        et_radius.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                et_radius.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        et_wireless_name.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                CommonUtils.hideKeyboard(activity!!)
            }

            true
        }

        ic_use_current_location.setOnClickListener {
            setGeofenceListener.onUseCurrentLocation()
        }

        btn_check.setOnClickListener {
            if (validate()) {
                if(!::setGeofenceListener.isInitialized){
                    return@setOnClickListener
                }

                setGeofenceListener.onEditSuccess(
                    Geofence(
                        LatLng(
                            et_latitude.text.toString().toDouble(),
                            et_longitude.text.toString().toDouble()
                        ),
                        et_radius.text.toString().toFloat(),
                        et_wireless_name.text.toString()
                    ),
                    LatLng(et_device_latitude.text.toString().toDouble(), et_device_longitude.text.toString().toDouble())
                )
            }
        }
    }

    fun setDeviceLocation(latitude: Double, longitude: Double){
        et_device_latitude.setText(latitude.toString())
        et_device_longitude.setText(longitude.toString())
    }

    fun validate(): Boolean {
        var validated = true

        try {
            var radius = et_radius.text.toString().toFloat()
        }catch (e: NumberFormatException){
            et_radius.error = getString(R.string.error_invalid_radius)
            validated = false
            et_radius.requestFocus()
        }

        try {
            var longitude = et_longitude.text.toString().toDouble()
            if (longitude < -180 || longitude > 180) {
                et_longitude.error = getString(R.string.error_invalid_longitude)
                validated = false
                et_longitude.requestFocus()
            }
        } catch (e: NumberFormatException) {
            et_longitude.error = getString(R.string.error_invalid_longitude)
            validated = false
            et_longitude.requestFocus()
        }

        try {
            var latitude = et_latitude.text.toString().toDouble()
            if (latitude < -90 || latitude > 90) {
                et_latitude.error = getString(R.string.error_invalid_latitude)
                validated = false
                et_latitude.requestFocus()
            }
        } catch (e: NumberFormatException) {
            et_latitude.error = getString(R.string.error_invalid_latitude)
            validated = false
            et_latitude.requestFocus()
        }

        try {
            var longitude = et_device_longitude.text.toString().toDouble()
            if (longitude < -180 || longitude > 180) {
                et_device_longitude.error = getString(R.string.error_invalid_longitude)
                validated = false
                et_device_longitude.requestFocus()
            }
        } catch (e: NumberFormatException) {
            et_device_longitude.error = getString(R.string.error_invalid_longitude)
            validated = false
            et_device_longitude.requestFocus()
        }

        try {
            var latitude = et_device_latitude.text.toString().toDouble()
            if (latitude < -90 || latitude > 90) {
                et_device_latitude.error = getString(R.string.error_invalid_latitude)
                validated = false
                et_device_latitude.requestFocus()
            }
        } catch (e: NumberFormatException) {
            et_device_latitude.error = getString(R.string.error_invalid_latitude)
            validated = false
            et_device_latitude.requestFocus()
        }

        return validated
    }
}
