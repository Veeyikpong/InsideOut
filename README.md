# InsideOut
An Android application that will detect if the device is located inside of a geofence area. Developed in Kotlin.

<img src="https://i.ibb.co/h2Z4GJN/screenshot-1563775509880.jpg" width="200" height="400"> <img src="https://i.ibb.co/2N3w31t/screenshot-1563775495401.jpg" width="200" height="400"> 
<img src="https://i.ibb.co/sCxJWmg/screenshot-1563775533432.jpg" width="200" height="400">

## How to use?
**A device is considered to be inside of the geofence area if the device is connected to the specified WiFi network or remains geographically inside the defined circle. 
NOTE: if device coordinates are reported outside of the zone, but the device still connected to the specific Wifi network, then the device is treated as being inside the geofence area.**
1. **Enter the device location**
	By default, the device location is set to your current location. You can enter your custom latitude and longitude. Besides, you can also set back to your current location by pressing this icon <img src="https://i.ibb.co/sRTNVfv/ic-my-location.png" width="20" height="20">
3. **Configure the geofence area**
Configure the geofence area by inputting **latitude, longitude and radius**. You can also enter a wifi network full name to check.
3. Press the **CHECK** button and you are good to go! App will display whether the device is inside / outside the geofence area, along with the determine factor (geographical location / wifi network).

## Architecture 
This app is developed based on android default MVC architecture, since it's only a single page application.
## Application Project files

	activity
	- MainActivity.kt #Rendering the map, and consists of all the important logic to check device location and geofence area
	- SplashActivity.kt #Splash screen
	
	fragment
	- SettingsFragment.kt #To capture and validate user input, then pass back to activity to process
	
	model
	- Geofence.kt #Geofence pojo class
	
	utils
	- AppConstants.kt #Stores all constant values to be used across application
	- CommonUtils.kt #Utility functions to be used across application
	- GeofencingPendingIntent.kt #Pending intent for geofencing, not really using this
	- SetGeofenceListener.kt #An interface, used to communicate between MainActivity and SettingsFragment

## Tests
I've included a UI test, (**GeofenceAreaTest.kt**) for this application, which will perform tests below:
 - Test when device located inside the geofence area, ignoring wifi name
 - Test when device does not located inside the geofence area, ignoring wifi name
 - Test when wifi network name does not match, but device is located geographically in the geofence area
 - Test when wifi network name does not match and device does not located inside the geofence area

[View preview here](https://i.ibb.co/4ZQ1qqh/insideout-uitest.gif)

## Libraries 

I've implemented some great libraries to complete this application.
- AndroidX support library
- Google Play Services (Maps)
- Google Play Services (Location)
- Google Material design library
- EasyPermissions ([https://github.com/googlesamples/easypermissions](https://github.com/googlesamples/easypermissions))
- Toasty ([https://github.com/GrenderG/Toasty](https://github.com/GrenderG/Toasty))
- AndroidX testing libraries 
   
