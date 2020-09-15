package com.codecool.holabusz.main

import android.util.Log
import com.codecool.holabusz.model.Stop
import com.codecool.holabusz.model.StopListResponse
import com.codecool.holabusz.model.StopResponse
import com.codecool.holabusz.network.RequestApi
import com.codecool.holabusz.network.RetrofitClient
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainPresenter() : MainContract.MainPresenter {

    var stops : MutableList<Stop> = mutableListOf()

    override val requestApi : RequestApi
        get() {
            return RetrofitClient.getRequestApi()
        }

    private var view : MainContract.MainView? = null
    //private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat : Double = 0.0
    private var lon : Double = 0.0

    override fun onAttach(view: MainContract.MainView) { this.view = view }
    override fun onDetach() { this.view = null }

    fun getStopObservable() : Observable<StopResponse> {
        return requestApi.getStopsForLocation(key = "apaiary-test",lon = 47.477900,lat=19.045807,radius = 50)
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun requestStops() {

        getStopObservable()
            .subscribe(object : io.reactivex.Observer<StopResponse?> {

                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: subscribed OK")
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, e.stackTraceToString())
                }

                override fun onComplete() {
                    try {
                        view?.successfullyLoaded()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onNext(stopResponse: StopResponse) {
                    var responseData : StopListResponse = stopResponse.data
                    var stopsData : List<Stop> = responseData.list

                    for (i in 0..stopsData.size-1) {
                        var id: String = stopsData.get(i).id
                        var name : String = stopsData.get(i).name

                        stops.add(Stop(id,name))
                    }
                }
            })
    }


    /*
    override fun getLat() : Double{
        return lat
    }

    override fun getLon(): Double {
        return lon
    }

     */

    companion object {
        private const val TAG = "MainPresenter"
    }

}


