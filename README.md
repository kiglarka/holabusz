# Holabusz Lite

![no_departures](https://github.com/kiglarka/holabusz/temp/no_vehicles.png) ![main](https://github.com/kiglarka/holabusz/temp/main.png) ![no_stops](https://github.com/kiglarka/holabusz/temp/no_vehno_stops.png)

## Description

This is an BKK API based, single activity application showing the closest stops with the soonest departing vehicles using the device current location at Budapest, Hungary.

## Tags 
#RecyclerView #RxJava #BKK #GoogleLocationServices #MVP #Kotlin #DI #Koin

## How to install
Feel free to install Holabusz.apk in the root folder on any Android devices.

## Actual Features

* The application has access to device's current location
* The user is able to define the desired maximum distance of the stops using he/she 's searching for with a seekbar
* Minimal design with BKK colors
* Handle Orientation changes

## Future Features

* An arrow to show the direction and the distance of stops
* Refresh upon gesture
* Favorite stops & vehicles and other custom user settings to save into SharedPreferences
* Flavours to introduce

## Buglist

* Longer stops' name interfers with the time of departure on the UI
* Need to disable seekbar during refresh or handle API call cancellation

## Sources

### BKK API
https://bkkfutar.docs.apiary.io/


