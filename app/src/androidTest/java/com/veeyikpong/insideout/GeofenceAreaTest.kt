package com.veeyikpong.insideout

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.veeyikpong.insideout.activity.MainActivity
import androidx.test.rule.ActivityTestRule
import com.veeyikpong.insideout.utils.AppConstants

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class GeofenceAreaTest {

    private val mRadius = 50f

    @Rule
    @JvmField
    var mActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup(){
        TestUtils.wakeDeviceUp()
    }

    /*
   * Test when device located inside the geofence area, ignoring wifi name
   */
    @Test
    fun testInside_Location() {
        onView(withId(R.id.et_device_latitude)).perform(clearText(), typeText(AppConstants.KUALA_LUMPUR_LOCATION.latitude.toString()))
        onView(withId(R.id.et_device_longitude)).perform(clearText(), typeText(AppConstants.KUALA_LUMPUR_LOCATION.longitude.toString()))

        onView(withId(R.id.et_latitude)).perform(clearText(), typeText("3.138675"))
        onView(withId(R.id.et_longitude)).perform(clearText(), typeText("101.616949"))

        onView(withId(R.id.et_radius)).perform(clearText(), typeText(mRadius.toString()))

        onView(withId(R.id.btn_check)).perform(click())

        //Sleep for 1 second to make sure the result is populated
        Thread.sleep(1000)

        onView(withId(R.id.tv_result)).check(matches(withText(R.string.inside)))
    }

    /*
    * Test when device does not located inside the geofence area, ignoring wifi name
    */
    @Test
    fun testOutside_Location() {
        onView(withId(R.id.et_device_latitude)).perform(clearText(), typeText(AppConstants.KUALA_LUMPUR_LOCATION.latitude.toString()))
        onView(withId(R.id.et_device_longitude)).perform(clearText(), typeText(AppConstants.KUALA_LUMPUR_LOCATION.longitude.toString()))

        onView(withId(R.id.et_latitude)).perform(clearText(), typeText("3.138675"))
        onView(withId(R.id.et_longitude)).perform(clearText(), typeText("101.6"))

        onView(withId(R.id.et_radius)).perform(clearText(), typeText(mRadius.toString()))

        onView(withId(R.id.btn_check)).perform(click())

        //Sleep for 1 second to make sure the result is populated
        Thread.sleep(1000)

        onView(withId(R.id.tv_result)).check(matches(withText(R.string.outside)))
    }

    /*
    * Test when wifi network name does not match, but device is located geographically in the geofence area
    */
    @Test
    fun testInside_Location_Wifi() {
        onView(withId(R.id.et_device_latitude)).perform(clearText(), typeText(AppConstants.KUALA_LUMPUR_LOCATION.latitude.toString()))
        onView(withId(R.id.et_device_longitude)).perform(clearText(), typeText(AppConstants.KUALA_LUMPUR_LOCATION.longitude.toString()))

        onView(withId(R.id.et_latitude)).perform(clearText(), typeText("3.138675"))
        onView(withId(R.id.et_longitude)).perform(clearText(), typeText("101.616949"))

        onView(withId(R.id.et_radius)).perform(clearText(), typeText(mRadius.toString()))
        onView(withId(R.id.et_wireless_name)).perform(clearText(), typeText("Pong"))

        onView(withId(R.id.btn_check)).perform(click())

        //Sleep for 1 second to make sure the result is populated
        Thread.sleep(1000)

        onView(withId(R.id.tv_result)).check(matches(withText(R.string.inside)))
    }

    /*
    * Test when wifi network name does not match and device does not located inside the geofence area
    */
    @Test
    fun testOutside_Location_Wifi() {
        onView(withId(R.id.et_device_latitude)).perform(clearText(), typeText(AppConstants.KUALA_LUMPUR_LOCATION.latitude.toString()))
        onView(withId(R.id.et_device_longitude)).perform(clearText(), typeText(AppConstants.KUALA_LUMPUR_LOCATION.longitude.toString()))

        onView(withId(R.id.et_latitude)).perform(clearText(), typeText("3.138675"))
        onView(withId(R.id.et_longitude)).perform(clearText(), typeText("101.6"))

        onView(withId(R.id.et_radius)).perform(clearText(), typeText(mRadius.toString()))
        onView(withId(R.id.et_wireless_name)).perform(clearText(), typeText("Pong"))

        onView(withId(R.id.btn_check)).perform(click())

        //Sleep for 1 second to make sure the result is populated
        Thread.sleep(1000)

        onView(withId(R.id.tv_result)).check(matches(withText(R.string.outside)))
    }
}
