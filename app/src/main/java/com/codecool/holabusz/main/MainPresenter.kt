package com.codecool.holabusz.main

import android.util.Log
import com.codecool.holabusz.model.*
import com.codecool.holabusz.network.RequestApi
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.math.acos

class MainPresenter(val requestApi: RequestApi) : MainContract.MainPresenter {

    private var stops: MutableList<Stop> = mutableListOf()
    private var departures: MutableList<Departure> = mutableListOf()

    private var view: MainContract.MainView? = null

    override fun onAttach(view: MainContract.MainView) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun refresh() {
        view?.showLoading()
        view?.checkPermission()
    }

    fun getAllStopObservable(currLat: Float, currLon: Float): Single<StopResponse> {
        return requestApi.getStopsForLocation(
            key = "apaiary-test",
            lat = currLat,
            lon = currLon,
            radius = 100
        )
    }

    override fun checkStops(currLat: Float, currLon: Float, maxDistance: Int) {
        val disposable = getAllStopObservable(currLat, currLon)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ stopResponse ->
                val responseData: StopListResponse = stopResponse.data
                val stopsData: List<Stop> = responseData.list

                val stopsRaw = stopsData

                    .filter {
                        meterDistanceBetweenPoints(
                            currLat,
                            currLon,
                            it.lat,
                            it.lon
                        ).toInt() <= maxDistance
                    }

                if (stopsRaw.isEmpty()) {
                    view?.hideLoading()
                    view?.setCenterMessage("There are no stops available in $maxDistance meters")
                } else {
                    view?.setCenterMessage("")
                    getComplexData(currLat, currLon, maxDistance)
                }
            },

                { e ->
                    Log.d(TAG, e.stackTraceToString())
                    view?.hideLoading()

                })
    }

    private fun getStopName(stops: List<Stop>, stopId: String): String {
        return stops.filter { it.id == stopId }.map { it.name }.joinToString()
    }

    private fun getRouteId(trips: List<Trip>, tripId: String): String {
        return trips.filter { it.id == tripId }.map { it.routeId }.joinToString()
    }

    private fun getCorrespondentRoute(routes: List<Routes>, routeId: String): Routes {
        return routes.first { it.id == routeId }
    }

    private fun getComplexData(currLat: Float, currLon: Float, maxDistance: Int) {
        val stopObservable = getAllStopObservable(currLat, currLon)
        val disposable = stopObservable

            .flatMap { stopResponse ->

                val responseData: StopListResponse = stopResponse.data
                val stopsData: List<Stop> = responseData.list

                val stopsRaw = stopsData

                    .filter {
                        meterDistanceBetweenPoints(
                            currLat,
                            currLon,
                            it.lat,
                            it.lon
                        ).toInt() <= maxDistance
                    }

                stops = stopsRaw.map {
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
                { departureResponse ->
                    // from departureResponse to stopTime
                    val responseData: DepartureListResponse = departureResponse.data
                    val departureData: StopTime = responseData.entry
                    val stopTime: List<Departure> = departureData.stopTimes

                    val trips = responseData.references.trips.map {
                            it.value
                            Trip(
                                it.value.id,
                                it.value.routeId
                            )
                        }

                    val routes =
                        responseData.references.routes.map {
                            Routes(
                                it.value.id,
                                it.value.shortName,
                                it.value.color
                            )
                        }

                    try {
                        departures = arrayListOf()
                        departures = stopTime.map {
                            Departure(
                                it.stopId,
                                getStopName(stops, it.stopId),
                                it.stopHeadsign,
                                it.departureTime,
                                it.tripId,
                                getRouteId(trips, it.tripId),
                                getCorrespondentRoute(
                                    routes,
                                    getRouteId(trips, it.tripId)
                                ).shortName,
                                "#" + getCorrespondentRoute(
                                    routes,
                                    getRouteId(trips, it.tripId)
                                ).color
                            )
                        }.toMutableList()

                        departures.apply {sortBy { departure ->  departure.departureTime}}

                    } catch (e: NullPointerException) {
                        view?.hideLoading()
                        view?.clearAdapter()
                        view?.setCenterMessage("There are no vehicles available")
                        return@subscribe
                    }


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

    override fun getNearestStopId(): String? {
        return stops.minByOrNull { it.distance }?.id
    }

    companion object {
        private const val TAG = "MainPresenter"
    }

}



