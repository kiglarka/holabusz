package com.codecool.holabusz.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.codecool.holabusz.R
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.lang.UnsupportedOperationException

class LocationService : Service() {

    private var locationCallback : LocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            if (locationResult != null && locationResult.lastLocation != null){
                var latitude : Double = locationResult.lastLocation.latitude
                var longtitude : Double = locationResult.lastLocation.longitude
                Log.d("Location", latitude.toString() + ","+ longtitude.toString())
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    private fun startLocationService(){
        val channelId = "location_notification_channel"
        val notificationManager : NotificationManager? = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val resultIntent = Intent()
        val pendingIntent : PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder : NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext,
            channelId
        )

        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("Location Service")
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        builder.setContentText("Running")
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(false)
        builder.setPriority(NotificationCompat.PRIORITY_MAX)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null){
                val notificationChannel : NotificationChannel = NotificationChannel(
                    channelId,
                    "Location Service",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.description = "This channel is used by location service"
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        val locationRequest : LocationRequest = LocationRequest()
        locationRequest.setInterval(4000)
        locationRequest.setFastestInterval(2000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        // permission?

        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())

        startForeground(Constants.LOCATION_SERVICE_ID, builder.build())
    }

    private fun stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this)
            .removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null){
            val action = intent.action
            if (action != null){
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) startLocationService()
                else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) stopLocationService()
            }
        }
        return super.onStartCommand(intent, flags, startId)

    }

}