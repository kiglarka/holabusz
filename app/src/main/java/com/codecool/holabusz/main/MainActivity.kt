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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codecool.holabusz.R
import com.codecool.holabusz.R.string
import com.codecool.holabusz.model.Departure
import com.codecool.holabusz.util.Constants
import com.codecool.holabusz.util.LocationService
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(), MainContract.MainView, SwipeRefreshLayout.OnRefreshListener {
    private val presenter : MainPresenter by inject()
    private var departureAdapter = DepartureAdapter(arrayListOf())

    data class Location(var lat: Double?, var lon: Double?)

    private var location = Variable(Location(0.0 ,0.0))
    private var maxDistance = 250

    class Variable<T>(private val defaultValue: T) {
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
            val lat = intent?.getDoubleExtra("LAT", 47.493414)
            val lon = intent?.getDoubleExtra("LON", 19.017302)
            if (lat != location.value.lat
                && lon != location.value.lon
            )
                location.value = Location(lat, lon)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.onAttach(this)

        hideAppBar()
        setAdapter()
        swiperefresh.setOnRefreshListener(this)
    }

    private fun setAdapter() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            //this.adapter = DepartureAdapter(arrayListOf())
            this.adapter = departureAdapter
        }
    }

    override fun hideAppBar() {
        supportActionBar?.hide()
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            locationReceiver,
            IntentFilter("LocationUpdate")
        )
        super.onResume()
        presenter.refresh()
        setSeekBarAction()

        //refreshData upon location change
        location.observable.subscribe {
            Log.d(TAG, "location changed:${location.value.lat}, ${location.value.lon}")
            if (location.value.lat !== 0.0 && location.value.lon !== 0.0) {
                refreshData()
            }
        }
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver)
        stopLocationService()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetach()
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
        seekBar.progress = maxDistance
        testText.text = seekBar.progress.toString() + getString(string.meter)

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                testText.text = progress.toString() + getString(string.meter)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                setCenterMessage("")
                showLoading()
                clearAdapter()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                maxDistance = seekBar?.progress ?: 250
                refreshData()
            }
        })
    }

    override fun makeToast(string: String){
        Toast.makeText(this,string,Toast.LENGTH_LONG).show()
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

    override fun clearAdapter(){
        departureAdapter.clearAdapter()
    }

    override fun setCenterMessage(text: String) {
        centerTextView.text = text
    }

    override fun showLoading() {
        swiperefresh.isRefreshing = true
    }

    override fun hideLoading() {
        swiperefresh.isRefreshing = false
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1
    }

    override fun onRefresh() {
        clearAdapter()
        refreshData()
    }




    private fun refreshData() {
        location.value.lat?.toFloat()?.let {
            location.value.lon?.toFloat()?.let { it1 ->
                presenter.checkStops(
                    it,
                    it1, maxDistance
                )
            }
        }
    }
}