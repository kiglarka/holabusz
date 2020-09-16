package com.codecool.holabusz.main

import android.util.Log
import com.codecool.holabusz.model.*
import com.codecool.holabusz.network.RequestApi
import com.codecool.holabusz.network.RetrofitClient
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.math.acos

class MainPresenter() : MainContract.MainPresenter {

    var stops: MutableList<Stop> = mutableListOf()

    override val requestApi: RequestApi
        get() {
            return RetrofitClient.getRequestApi()
        }

    private var view: MainContract.MainView? = null

    private var lat: Double = 0.0
    private var lon: Double = 0.0

    override fun onAttach(view: MainContract.MainView) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }


    fun getStopObservable(): Single<StopResponse> {
        return requestApi.getStopsForLocation(
            key = "apaiary-test",
            lon = 47.477900,
            lat = 19.045807,
            radius = 100
        )

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    /*
    fun getDepartureObservable(): Single<DepartureResponse> {
        return requestApi.getArrivalsAndDeparturesForStop(
            key = "apaiary-test", stopId = nearbyStopIds().joinToString(
                "&stopId="),limit = 60)
    }
    
     */

    fun getDepartures(currLat: Float, currLon: Float) {

        val observableSecond = requestApi.getArrivalsAndDeparturesForStop(
            key = "apaiary-test", stopId = listOf("BKK_F02461").joinToString(
                "&stopId="
            ), limit = 60
        )

        val result = observableSecond.toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                // onNext
                { n ->
                    println(n)
                    // TODO: 2020.09.16. yeah 


                },
                // onError
                { e -> e.printStackTrace() },
                //OnComplete
                { view?.successfullyLoaded() }
            )


    }


    fun getComplexData(currLat: Float, currLon: Float) {


        val observableFirst = requestApi.getStopsForLocation(
            key = "apaiary-test",
            lon = 47.477900,
            lat = 19.045807,
            radius = 100
        )

        /*
        val observableSecond = requestApi.getArrivalsAndDeparturesForStop(
            key = "apaiary-test", stopId = nearbyStopIds().joinToString(
                "&stopId="
            ), limit = 60
        )

         */


        var result = observableFirst
            /*
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

             */
            .flatMap { stopResponse ->

                var responseData: StopListResponse = stopResponse.data
                var stopsData: List<Stop> = responseData.list

                stops = stopsData.map {
                    Stop(
                        it.id,
                        it.name,
                        it.direction,
                        it.lat,
                        it.lon,
                        meterDistanceBetweenPoints(currLat, currLon, it.lat, it.lon)
                    )
                }.toMutableList()

                requestApi.getArrivalsAndDeparturesForStop(
                    key = "apaiary-test", stopId = stops.map { it.id }.joinToString(
                        "&stopId="
                    ), limit = 60
                )
            }

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribe(
                {
                    try {
                        view?.successfullyLoaded()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                { e ->
                    // TODO: 2020.09.16. to log the url somewhere
                    Log.d(TAG, e.stackTraceToString())
                    view?.hideLoading()

                }

            )
    }

    private fun meterDistanceBetweenPoints(
        currLat: Float,
        currLon: Float,
        stopLat: Float,
        stopLon: Float
    ): Double {
        val pk: Float = (180F / Math.PI).toFloat()

        val a1: Float = currLat / pk
        val a2: Float = currLon / pk
        val b1: Float = stopLat / pk
        val b2: Float = stopLon / pk

        val t1: Double =
            Math.cos(a1.toDouble()) * Math.cos(a2.toDouble()) * Math.cos(b1.toDouble()) * Math.cos(
                b2.toDouble()
            )

        val t2: Double =
            Math.cos(a1.toDouble()) * Math.sin(a2.toDouble()) * Math.cos(b1.toDouble()) * Math.sin(
                b2.toDouble()
            )

        val t3: Double = Math.sin(a1.toDouble()) * Math.sin(b1.toDouble())

        val tt: Double = acos(t1 + t2 + t3)

        return 6366000 * tt
    }

    override fun filterNearByStops(meters: Int): List<Stop> {
        return stops.filter { it.distance <= meters }
    }


    companion object {
        private const val TAG = "MainPresenter"
    }

}


