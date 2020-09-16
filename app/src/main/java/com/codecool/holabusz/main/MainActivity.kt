package com.codecool.holabusz.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecool.holabusz.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainContract.MainView {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var presenter: MainPresenter
    //var heresTheBus : MutableList<Stop> = mutableListOf<Stop>()
    //var stopCount : Int = 0

    private var lat : Float = 0.0F
    private var lon : Float = 0.0F

    override fun provideCurrentLat(): Float { return lat }
    override fun provideCurrentLon(): Float { return lon }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        showLoading()

        presenter = MainPresenter()
        presenter.onAttach(this)

    }

    override fun onResume() {
        super.onResume()

        presenter.getDepartures(lat,lon)
        // presenter.getComplexData(lat,lon)

    }

    override fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this@MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this@MainActivity)
            fusedLocationClient.lastLocation

                .addOnSuccessListener { location ->
                    if (location != null) {
                        lat = location.latitude.toFloat()
                        lon = location.longitude.toFloat()
                        Log.d(TAG, "onRequestPermissionsResult: $lat")
                        Log.d(TAG, "onRequestPermissionsResult: $lon")
                    } else {
                        Log.d(
                            TAG,
                            "onRequestPermissionsResult: nincs location!!! $location"
                        )
                    }
                }
        }


        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }

    override fun successfullyLoaded() {
        hideLoading()
        setAdapter()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


    private fun setAdapter(){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            // TODO: 2020.09.15. filters to put to another class 
            //val allStops = presenter.stops
            val filtered = presenter.filterNearByStops(250)
            adapter = MainAdapter(filtered)
            testText.text = filtered.size.toString()
            adapter = adapter
        }
    }



    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        Thread.sleep(1000)
        progressBar.visibility = View.GONE
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}