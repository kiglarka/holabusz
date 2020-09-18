package com.codecool.holabusz.main

import android.util.Log
import androidx.core.text.htmlEncode
import com.codecool.holabusz.model.*
import com.codecool.holabusz.network.RequestApi
import com.codecool.holabusz.network.RetrofitClient
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okio.ByteString.Companion.encodeUtf8
import kotlin.math.acos

class MainPresenter() : MainContract.MainPresenter {

    var stops: MutableList<Stop> = mutableListOf()
    var departures: MutableList<Departure> = mutableListOf()

    override val requestApi: RequestApi
        get() {
            return RetrofitClient.getRequestApi()
        }

    private var view: MainContract.MainView? = null

    override fun onAttach(view: MainContract.MainView) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun firstRun() {
        view?.hideAppBar()
        view?.showLoading()
        view?.checkPermission()
    }


    fun getStopObservable(currLat: Float, currLon: Float): Single<StopResponse> {
        return requestApi.getStopsForLocation(
            key = "apaiary-test",
            lat = currLat,
            lon = currLon,
            radius = 100
        )


    }

    fun getDepartureObservable(): Single<DepartureResponse> {

        val stopIdValue = listOf("BKK_F00412", "BKK_F02461").joinToString("&stopId=")
        Log.d(TAG, "stopId: $stopIdValue")
        return requestApi.getArrivalsAndDeparturesForStop(
            key = "apaiary-test", stopId = stopIdValue, limit = 60
        )

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun getStops(lat: Float, lon: Float) {

        Log.d(TAG, "getStops: currlat $lat")
        Log.d(TAG, "getStops: currlon $lon")

        var result = getStopObservable(lat, lon)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ stopResponse ->
                val responseData: StopListResponse = stopResponse.data
                val stopsData: List<Stop> = responseData.list

                stops = stopsData.map {
                    Stop(
                        it.id,
                        it.name,
                        it.direction,
                        it.lat,
                        it.lon,
                        meterDistanceBetweenPoints(lat, lon, it.lat, it.lon)
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

    private fun getStopName(stops: List<Stop>, stopId: String) : String {
        return stops.filter{ it.id == stopId}.map { it.name }.joinToString()
    }

    private fun getRouteId(trips: List<Trip>, tripId: String): String {
        return trips.filter { it.id == tripId }.map { it.routeId }.joinToString()
    }

    private fun getCorrespondentRoute(routes: List<Routes>, routeId: String) : Routes {
        return routes.first { it.id == routeId }
    }

    override fun getComplexData(currLat: Float, currLon: Float, maxDistance: Int) {

        view?.showLoading()
        val stopObservable = getStopObservable(currLat,currLon)

        var result = stopObservable
            /*
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
             */
            .flatMap { stopResponse ->

                val responseData: StopListResponse = stopResponse.data
                val stopsData: List<Stop> = responseData.list

                stops = stopsData

                    .filter { meterDistanceBetweenPoints(currLat, currLon, it.lat, it.lon).toInt() <= maxDistance }
                    .map {

                    Stop(
                        it.id,
                        it.name,
                        it.direction,
                        it.lat,
                        it.lon,
                        meterDistanceBetweenPoints(currLat, currLon, it.lat, it.lon)
                    )
                }.toMutableList()

                Log.d(TAG, "stopsIds: ${stops.map { it.id }}")
                requestApi.getArrivalsAndDeparturesForStop(
                    key = "apaiary-test", stopId = stops.map { it.id }.joinToString(
                        "&stopId="
                    ), limit = 60
                )

            }

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

            .subscribe(
                { departureResponse ->
                    // from departureResponse to stopTime
                    val responseData: DepartureListResponse = departureResponse.data
                    val departureData: StopTime = responseData.entry
                    val stopTime: List<Departure> = departureData.stopTimes

                    val references = responseData.references


                    val trips =
                    responseData.references.trips.map {
                        it.value
                        Trip (
                            it.value.id,
                            it.value.routeId
                        )
                    }


                    val routes =
                        responseData.references.routes.map {
                            Routes (
                                it.value.id,
                                it.value.shortName,
                                it.value.color
                            )
                        }



                    departures = stopTime.map {
                        Departure(
                            it.stopId,
                            getStopName(stops,it.stopId),
                            it.stopHeadsign,
                            it.departureTime,
                            it.tripId,
                            getRouteId(trips,it.tripId),
                            getCorrespondentRoute(routes, getRouteId(trips,it.tripId)).shortName,
                            "#"+ getCorrespondentRoute(routes, getRouteId(trips,it.tripId)).color


                        )
                    }.toMutableList()

                    try {
                        view?.hideLoading()
                        //view?.setAdapter(filterNearByStops(250))
                        view?.setAdapterWithData(departures)

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

    override fun getNearestStopId(): String? {
        return stops.minByOrNull { it.distance }?.id
    }


    companion object {
        private const val TAG = "MainPresenter"
    }

}



