package com.codecool.holabusz.main

import com.codecool.holabusz.model.Departure
import com.codecool.holabusz.model.Stop
import com.codecool.holabusz.network.RequestApi
import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty

interface MainContract {

    interface MainView {

        fun showLoading()
        fun hideLoading()
        fun checkPermission()
        fun hideAppBar()
        fun setSeekBarAction()
        fun makeToast(string: String)
        fun setCenterMessage(text: String)
        fun setAdapterWithData(data: List<Departure>)
        fun clearAdapter()
    }

    interface MainPresenter {

        fun onAttach(view: MainContract.MainView)
        fun onDetach()
        fun firstRun()
        fun filterNearByStops(meters: Int): List<Stop>
        fun getNearestStopId(): String?
        fun checkStops(currLat: Float, currLon: Float, maxDistance: Int)
    }

}