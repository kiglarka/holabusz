package com.codecool.holabusz.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecool.holabusz.R
import com.codecool.holabusz.model.Model
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainContract.MainView {

    private lateinit var presenter: MainPresenter
    private lateinit var adapter: MainAdapter
    var heresTheBus : List<Model> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()

        presenter = MainPresenter()
        presenter.onAttach(this)

        adapter.submitList(presenter.getData())
    }

    private fun initRecyclerView(){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MainAdapter()
            adapter = adapter
        }
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        Thread.sleep(1000)
        progressBar.visibility = View.GONE
    }

}