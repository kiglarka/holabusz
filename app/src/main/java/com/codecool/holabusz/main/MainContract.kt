package com.codecool.holabusz.main

interface MainContract {

    interface MainView {

        fun showLoading()
        fun hideLoading()
    }

    interface MainPresenter {

        fun onAttach(view: MainView)
        fun onDetach()
    }

}