package com.codecool.holabusz.main

import android.content.Context
import java.lang.ref.WeakReference

interface MainContract {

    interface MainView {

        fun showLoading()
        fun hideLoading()
        fun checkPermission()
    }

    interface MainPresenter {

        fun onAttach(view: MainView)
        fun onDetach()
        fun initializeFusedLocationCLinet(activity: MainActivity)

        fun getLat(): Double
        fun getLon(): Double
        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        )

        fun checkPermission(weakContext: WeakReference<Context>)
    }

}