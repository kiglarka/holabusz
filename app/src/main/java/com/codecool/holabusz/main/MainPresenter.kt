package com.codecool.holabusz.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codecool.holabusz.network.RequestApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.ref.WeakReference

class MainPresenter() : MainContract.MainPresenter {

    private lateinit var conti : WeakReference<Context>

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private val mainActivity : MainActivity = MainActivity()


    private var view : MainContract.MainView? = null
    private lateinit var requestApi : RequestApi

    override fun onAttach(view: MainContract.MainView) { this.view = view}
    override fun onDetach() { this.view = null }

    override fun initializeFusedLocationCLinet(activity: MainActivity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    override fun checkPermission(weakContext: WeakReference<Context>) {
        conti = weakContext
        if (ContextCompat.checkSelfPermission(
                conti.get()!!,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(mainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(mainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            conti.get()!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(
                            conti.get()!!,
                            "Permission Granted",
                            Toast.LENGTH_SHORT
                        ).show()

                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location ->
                                if (location != null) {
                                    lat = location.altitude
                                    lon = location.longitude
                                    Log.d(TAG, "onRequestPermissionsResult: $lat")
                                    Log.d(TAG, "onRequestPermissionsResult: $lon")
                                }
                            }
                    }
                } else {
                    Toast.makeText(conti.get()!!, "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }
        }
    }


    override fun getLat() : Double{
        return lat
    }

    override fun getLon(): Double {
        return lon
    }

    companion object {
        private const val TAG = "MainPresenter"
    }

}