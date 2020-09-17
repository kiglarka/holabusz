package com.codecool.holabusz.main

import android.util.Log
import com.codecool.holabusz.model.*
import com.codecool.holabusz.network.RequestApi
import com.codecool.holabusz.network.RetrofitClient
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.math.acos

class MainPresenter() : MainContract.MainPresenter {

    var stops: MutableList<Stop> = mutableListOf()
    var departures: MutableList<Departure> = mutableListOf()

    override val requestApi: RequestApi
        get() {
            return RetrofitClient.getRequestApi()
        }

    private var view: MainContract.MainView? = null

    private var lat: Float = 0.0F
    private var lon: Float = 0.0F

    override fun onAttach(view: MainContract.MainView) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }


    fun getStopObservable(currLat: Float, currLon: Float): Single<StopResponse> {
        return requestApi.getStopsForLocation(
            key = "apaiary-test",
            lat = currLat,
            lon = currLon,
            radius = 100
        )

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getDepartureObservable(): Single<DepartureResponse> {
        return requestApi.getArrivalsAndDeparturesForStop(
            key = "apaiary-test", stopId = listOf("BKK_F00412").joinToString(
                "&stopId="
            ), limit = 60
        )

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun getStops(lat: Float, lon: Float) {

        Log.d(TAG, "getStops: currlat $lat")
        Log.d(TAG, "getStops: currlon $lon")


        var result = getStopObservable(lat,lon)
            .subscribe( {
                stopResponse ->
                val responseData: StopListResponse = stopResponse.data
                val stopsData: List<Stop> = responseData.list

                stops = stopsData.map {
                    Stop(
                        it.id,
                        it.name,
                        it.direction,
                        it.lat,
                        it.lon,
                        meterDistanceBetweenPoints(lat, lon, it.lat, it.lon).toDouble()
                    )
                }.toMutableList()

                try {
                    view?.hideLoading()
                    view?.setAdapter(filterNearByStops(250))

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            },

                { e ->
                    Log.d(TAG, e.stackTraceToString())
                    view?.hideLoading()

                })
    }


    fun getDepartures() {

        var result2 = getDepartureObservable()
            .subscribe(
                { departureResponse ->
                    val responseData: DepartureListResponse = departureResponse.data
                    val departureData: StopTime = responseData.entry
                    val stopTime: List<Departure> = departureData.stopTimes

                    departures = stopTime.map {
                        Departure(

                            it.stopHeadsign,
                            it.departureTime,
                            it.tripId
                        )

                    }.toMutableList()

                    try {
                        view?.hideLoading()
                        view?.setAdapterWithData(departures)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                { e ->
                    Log.d(TAG, e.stackTraceToString())
                    view?.hideLoading()

                })


        /*
        getDepartureObservable().toObservable()
            .subscribe(object : Observer<DepartureResponse?> {

                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: subscribed OK")
                }

                override fun onNext(departureResponse: DepartureResponse) {
                    val responseData: DepartureListResponse = departureResponse.data
                    val departureData: StopTime = responseData.entry
                    val stopTime: List<Departure> = departureData.stopTimes

                    departures = stopTime.map {
                        Departure(

                            it.stopHeadsign,
                            it.departureTime,
                            it.tripId
                        )

                    }.toMutableList()
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, e.stackTraceToString())
                }

                override fun onComplete() {
                    try {
                        Log.d(TAG, departures.toString())
                        view?.setAdapterWithData(departures)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })

         */
    }


    fun getComplexData(currLat: Float, currLon: Float) {


        val observableFirst = requestApi.getStopsForLocation(
            key = "apaiary-test",
            lon = currLon,
            lat = currLat,
            radius = 100
        )

        var result = observableFirst
            /*
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

             */
            .flatMap { stopResponse ->

                val responseData: StopListResponse = stopResponse.data
                val stopsData: List<Stop> = responseData.list

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
                        view?.hideLoading()
                        view?.setAdapter(filterNearByStops(250))

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
        return stops.filter { it.distance.toInt() <= meters }
    }


    companion object {
        private const val TAG = "MainPresenter"
    }

}



