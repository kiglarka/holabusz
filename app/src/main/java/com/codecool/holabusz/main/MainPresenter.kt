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