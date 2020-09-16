package com.codecool.holabusz.model

data class StopResponse(val status : String, val currentTime : String, val data : StopListResponse)
data class DepartureResponse(val status : String, val currentTime : String, val data : DepartureListResponse)

data class StopListResponse(val list : List<Stop>)
data class DepartureListResponse(val entry : List<Departure>)

data class Stop(
    val id: String,
    val name: String,
    val direction: String,
    val lat: Float,
    val lon: Float,
    val distance: Double
)

data class Departure(
    val stopId: String,
    val stopHeadsign: String,
    val departureTime: String,
    val tripId: String
)