package com.codecool.holabusz.main

import android.Manifest
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecool.holabusz.R
import com.codecool.holabusz.model.Departure
import com.codecool.holabusz.model.Stop
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), MainContract.MainView {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val departureAdapter = DepartureAdapter(arrayListOf())
    private lateinit var presenter: MainPresenter

    private var lat : Double = 47.516064
    private var lon : Double = 19.056467

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter()
        presenter.onAttach(this)

        hideAppBar()

        setSeekBarAction()

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            //adapter = DepartureAdapter(arrayListOf())
            this.adapter = departureAdapter

        }

        presenter.firstRun()


    }

    override fun hideAppBar() {
        getSupportActionBar()?.hide()
    }

    override fun onResume() {
        super.onResume()

        // presenter.getStops(lat.toFloat(),lon.toFloat())
        // presenter.getDepartures()

    }

    override fun setSeekBarAction() {
        seekBar.progress = 250
        testText.text = seekBar.progress.toString() + " m"


        seekBar.setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                testText.text = progress.toString() + " m"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val maxDistance = seekBar?.progress?.let{
                    presenter.getComplexData(lat.toFloat(),lon.toFloat(), it)
                }


            }

        })
    }


    override fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this@MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this@MainActivity)
            fusedLocationClient.lastLocation

                    //to observable!!!

                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d(TAG, "onRequestPermissionsResult: ${location.latitude}")
                        Log.d(TAG, "onRequestPermissionsResult: ${location.longitude}")
                        presenter.getComplexData(location.latitude.toFloat(),location.longitude.toFloat(),250)

                    } else {
                        Log.d(
                            TAG,
                            "onRequestPermissionsResult: nincs location!!! $location"
                        )
                        Toast.makeText(this,"No location detected",Toast.LENGTH_LONG).show()
                    }
                }
        }


        else if (ContextCompat.checkSelfPermission(this@MainActivity,
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

    override fun setAdapterWithData(data : List<Departure>){
        departureAdapter.setDepartures(data)
    }

    override fun setAdapter(data : List<Stop>){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            // TODO: 2020.09.15. filters to put to another class
            adapter = MainAdapter(data)
            testText.text = data.size.toString()
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