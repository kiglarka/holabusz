package com.codecool.holabusz.main

import com.codecool.holabusz.network.RequestApi
import com.codecool.holabusz.network.RetrofitClient

class MainPresenter(var view: MainContract.MainView?) : MainContract.MainPresenter {

    /*
    private lateinit var conti : WeakReference<Context>

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat : Double = 0.0
    private var lon : Double = 0.0
 */


    //private var view : MainContract.MainView? = null
    private var requestApi : RequestApi = RetrofitClient.create()

    override fun onAttach(view: MainContract.MainView) { this.view = view}
    override fun onDetach() { this.view = null }

    fun subscribe(){
        requestApi.getStopsForLocation(key = "apaiary-test",lon = 47.477900,lat=19.045807,radius = 100)

    }

    /*
    override fun initializeFusedLocationCLinet(activity: MainActivity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }

     */



    /*
    override fun getLat() : Double{
        return lat
    }

    override fun getLon(): Double {
        return lon
    }

     */

    companion object {
        private const val TAG = "MainPresenter"
    }

}