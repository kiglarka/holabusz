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
import kotlin.math.acos

class MainPresenter() : MainContract.MainPresenter {

    var stops : MutableList<Stop> = mutableListOf()

    override val requestApi : RequestApi
        get() {
            return RetrofitClient.getRequestApi()
        }

    private var view : MainContract.MainView? = null

    private var lat : Double = 0.0
    private var lon : Double = 0.0

    override fun onAttach(view: MainContract.MainView) { this.view = view }
    override fun onDetach() { this.view = null }

    fun getStopObservable() : Observable<StopResponse> {
        return requestApi.getStopsForLocation(key = "apaiary-test",lon = 47.477900,lat=19.045807,radius = 100)
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
                    stops.clear()

                    var responseData : StopListResponse = stopResponse.data
                    var stopsData : List<Stop> = responseData.list

                    for (i in 0 until stopsData.size) {
                        val id: String = stopsData.get(i).id
                        val name : String = stopsData.get(i).name
                        val direction = stopsData.get(i).direction
                        val lat: Float = stopsData.get(i).lat.toFloat()
                        val lon: Float = stopsData.get(i).lon.toFloat()

                        val distance = meterDistanceBetweenPoints(view?.provideCurrentLat()!!,view?.provideCurrentLon()!!,lat,lon)

                        stops.add(Stop(id,name,direction,lat,lon,distance))
                    }
                }
            })
    }

    private fun meterDistanceBetweenPoints(currLat : Float, currLon: Float, stopLat : Float, stopLon : Float) : Double {
        val pk : Float = (180F / Math.PI).toFloat()

        val a1 : Float = currLat / pk
        val a2 : Float = currLon / pk
        val b1 : Float = stopLat / pk
        val b2 : Float = stopLon / pk

        val t1 : Double = Math.cos(a1.toDouble()) * Math.cos(a2.toDouble()) * Math.cos(b1.toDouble()) * Math.cos(
            b2.toDouble())

        val t2 : Double = Math.cos(a1.toDouble()) * Math.sin(a2.toDouble()) * Math.cos(b1.toDouble()) * Math.sin(
            b2.toDouble()
        )

        val t3 : Double = Math.sin(a1.toDouble()) * Math.sin(b1.toDouble())

        val tt : Double = acos(t1 + t2 + t3)

        return 6366000 * tt


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


