package com.codecool.holabusz.main

import android.Manifest
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecool.holabusz.R
import com.codecool.holabusz.model.Departure
import com.codecool.holabusz.util.Constants
import com.codecool.holabusz.util.LocationService
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainContract.MainView {

    data class Location(var lat: Double = 0.0, var lon: Double = 0.0)

    private var location = Variable(Location(0.0,0.0))

    inner class Variable<T>(private val defaultValue: T) {
        var value: T = defaultValue
            set(value) {
                field = value
                observable.onNext(value)
            }
        val observable = BehaviorSubject.createDefault(value)
    }


    private var locationReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            // get extra data from intent

            var lat = intent?.getDoubleExtra("LAT", 47.493414)
            var lon = intent?.getDoubleExtra("LON", 19.017302)
            if (lat != location.value.lat && lon != location.value.lon) location.value = Location(lat!!,lon!!)

            Log.d(TAG, "onReceive: $lat,$lon")
        }
    }

    val departureAdapter = DepartureAdapter(arrayListOf())
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter()
        presenter.onAttach(this)

        hideAppBar()

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            //adapter = DepartureAdapter(arrayListOf())
            this.adapter = departureAdapter

        }

        presenter.firstRun()

    }


    override fun hideAppBar() {
        supportActionBar?.hide()
    }

    override fun onResume() {

        location.observable.subscribe{
            Log.d(TAG, "location changed:${location.value.lat}, ${location.value.lon}")
            presenter.getComplexData(location.value.lat.toFloat(),location.value.lon.toFloat(),250)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            locationReceiver,
            IntentFilter("LocationUpdate")
        )

        super.onResume()
        setSeekBarAction()

    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver)
        super.onPause()
    }

    private fun isLocationServiceRunning(): Boolean {
        val activityManager: ActivityManager? =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        if (activityManager != null) {
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if (LocationService::class.java.name.equals(service.service.className)) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }
        return false
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE)
            startService(intent)
            Toast.makeText(this, "Location Service Started", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationService() {
        if (isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE)
            startService(intent)
            Toast.makeText(this, "Location Service Stopped", Toast.LENGTH_SHORT).show()
        }
    }

    override fun setSeekBarAction() {
        seekBar.progress = 250
        testText.text = seekBar.progress.toString() + " m"
        //lat?.toFloat()?.let { lon?.toFloat()?.let { it1 -> presenter.getComplexData(it, it1,250) } }


        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                testText.text = progress.toString() + " m"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                val maxDistance = seekBar?.progress?.let {
                    location.value.lat?.toFloat()?.let { it1 ->
                        location.value.lon?.toFloat()?.let { it2 ->
                            presenter.getComplexData(
                                it1,
                                it2, it
                            )
                        }
                    }
                }

            }

        })
    }


    override fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationService()

        } else if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
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

    override fun setAdapterWithData(data: List<Departure>) {
        departureAdapter.setDepartures(data)

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
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1
    }

}