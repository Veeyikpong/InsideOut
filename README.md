# InsideOut
An Android application that will detect if the device is located inside of a geofence area. Developed in Kotlin.

![Splash screen](https://picasaweb.google.com/113570045601075403944/6716365103948690705#6716365109274960962 "Splash screen")

![Inside geofence area](https://picasaweb.google.com/113570045601075403944/6716365310097068401#6716365317196385106 "Inside geofence area")

![Outside geofence area](https://picasaweb.google.com/113570045601075403944/6716365512629972225#6716365519730607170 "Outside geofence area")

## How to use?
**A device is considered to be inside of the geofence area if the device is connected to the specified WiFi network or remains geographically inside the defined circle. 
NOTE: if device coordinates are reported outside of the zone, but the device still connected to the specific Wifi network, then the device is treated as being inside the geofence area.**
1. **Enter the device location**
	By default, the device location is set to your current location. You can enter your custom latitude and longitude. Besides, you can also set back to your current location by pressing ![Current location icon](https://picasaweb.google.com/113570045601075403944/6716366623707512433#6716366625687554354 "Current Location icon")
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
 
![UI Test](https://picasaweb.google.com/113570045601075403944/6716376233999240785#6716376239497460322 "UI Test")

## Libraries 

I've implemented some great libraries to complete this application.
- AndroidX support library
- Google Play Services (Maps)
- Google Play Services (Location)
- Google Material design library
- EasyPermissions ([https://github.com/googlesamples/easypermissions](https://github.com/googlesamples/easypermissions))
- Toasty ([https://github.com/GrenderG/Toasty](https://github.com/GrenderG/Toasty))
- AndroidX testing libraries 
   
