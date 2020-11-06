package com.codecool.holabusz.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class StopResponse(val status: String, val currentTime: String, val data: StopListResponse)
data class StopListResponse(val list: List<Stop>)

data class Stop(
    val id: String,
    val name: String,
    val direction: String,
    val lat: Float,
    val lon: Float,
    val distance: Double
)


data class DepartureResponse(
    val status: String,
    val currentTime: String,
    val data: DepartureListResponse
)

data class DepartureListResponse(val entry: StopTime, val references: References)

data class StopTime(val stopTimes: List<Departure>)

data class References(val trips: Map<String, Trip>, val routes: Map<String, Routes>)

/*
data class DepartureRefTripResponse(val trips : Trip)
data class DepartureRefRouteResponse(val routes : List<Routes>)
 */

@Parcelize
data class Departure(
    val stopId: String,
    val stopName: String,
    val stopHeadsign: String,
    val departureTime: Int,
    val tripId: String,
    val routeId: String,
    val shortName: String,
    val color: String
) : Parcelable

data class Routes(
    val id: String,
    val shortName: String,
    val color: String,
)

data class Trip(
    val id: String,
    val routeId: String
)


