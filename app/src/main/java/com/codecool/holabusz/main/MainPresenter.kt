package com.codecool.holabusz.main

import com.codecool.holabusz.network.RequestApi

class MainPresenter : MainContract.MainPresenter {

    private var view : MainContract.MainView? = null
    private lateinit var requestApi : RequestApi

    override fun onAttach(view: MainContract.MainView) { this.view = view}
    override fun onDetach() { this.view = null }

}