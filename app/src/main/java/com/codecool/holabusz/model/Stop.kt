package com.codecool.holabusz.model

data class StopResponse(val status : String, val currentTime : String, val data : StopListResponse)

data class StopListResponse(val list : List<Stop>)

data class Stop(
    val id: String,
    val name: String,
    val direction: String,
    val lat: Float,
    val lon: Float,
    val distance: Double
)