package com.codecool.holabusz.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.codecool.holabusz.R
import com.codecool.holabusz.model.Model
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainContract.MainView {

    lateinit var presenter: MainPresenter
    var heresTheBus : List<Model> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: 2020.09.01. yet to setup adapter 

        presenter = MainPresenter()
        presenter.onAttach(this)
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        Thread.sleep(1000)
        progressBar.visibility = View.GONE
    }
}