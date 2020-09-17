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
    }

    interface MainPresenter {

        fun onAttach(view: MainContract.MainView)
        fun onDetach()
        val requestApi: RequestApi
        fun filterNearByStops(meters: Int): List<Stop>
    }

}