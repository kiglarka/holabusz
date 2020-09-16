package com.codecool.holabusz.main

import android.content.Context
import com.codecool.holabusz.model.Departure
import com.codecool.holabusz.model.Stop
import com.codecool.holabusz.network.RequestApi
import java.lang.ref.WeakReference

interface MainContract {

    interface MainView {

        fun showLoading()
        fun hideLoading()
        fun checkPermission()
        fun successfullyLoaded()
        fun provideCurrentLat(): Float
        fun provideCurrentLon(): Float
        fun setAdapter()
        fun setAdapterWithData(data: List<Departure>)
    }

    interface MainPresenter {

        fun onAttach(view: MainContract.MainView)
        fun onDetach()
        val requestApi: RequestApi
        //fun requestStops()
        fun filterNearByStops(meters: Int): List<Stop>
    }

}