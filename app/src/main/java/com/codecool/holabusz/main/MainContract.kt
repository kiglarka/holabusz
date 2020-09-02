package com.codecool.holabusz.main

import android.content.Context
import java.lang.ref.WeakReference

interface MainContract {

    interface MainView {

        fun showLoading()
        fun hideLoading()
        fun checkPermission()
        val presenter: com.codecool.holabusz.main.MainPresenter
    }

    interface MainPresenter {

        fun onAttach(view: MainView)
        fun onDetach()

    }

}