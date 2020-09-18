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
        fun setAdapter(data: List<Stop>)
        fun setAdapterWithData(data: List<Departure>)
        fun hideAppBar()
        fun setSeekBarAction()
    }

    interface MainPresenter {

        fun onAttach(view: MainContract.MainView)
        fun onDetach()
        fun firstRun()
        val requestApi: RequestApi
        fun filterNearByStops(meters: Int): List<Stop>
        fun getNearestStopId(): String?
        fun getComplexData(currLat: Float, currLon: Float, maxDistance: Int)
    }

}