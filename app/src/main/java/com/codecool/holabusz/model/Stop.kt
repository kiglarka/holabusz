package com.codecool.holabusz.model



data class StopResponse(val status : String, val currentTime : String, val data : StopListResponse)
data class DepartureResponse(val status : String, val currentTime : String, val data : DepartureListResponse)

data class StopListResponse(val list : List<Stop>)
data class DepartureListResponse(val entry : StopTime)
data class DepartureReferenceResponse(val references : StopTime)
data class DepartureRefTripResponse(val trips : List<Trip>)
data class DepartureRefRouteResponse(val routes : List<Routes>)

data class StopTime(val stopTimes: List<Departure>)

data class Routes(
    val id: String,
    val shortName: String,
    val color: String,
)

data class Trip(
    val id: String,
    val routeId: String
)

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
    val stopName: String,
    val stopHeadsign: String,
    val departureTime: Int,
    val tripId: String,
)