package com.veeyikpong.insideout

import android.graphics.Point
import android.os.RemoteException
import androidx.test.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice

class TestUtils {
    companion object{
        fun wakeDeviceUp(){
            val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            val coordinates = arrayOfNulls<Point>(4)
            coordinates[0] = Point(248, 1520)
            coordinates[1] = Point(248, 929)
            coordinates[2] = Point(796, 1520)
            coordinates[3] = Point(796, 929)
            try {
                if (!uiDevice.isScreenOn) {
                    uiDevice.wakeUp()
                    uiDevice.swipe(coordinates, 10)
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }
}