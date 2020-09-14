package com.codecool.holabusz.network

import android.graphics.ColorSpace
import com.codecool.holabusz.model.StopResponse
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface RequestApi {

    @GET("arrivals-and-departures-for-location.json?" +
                "key=key" +
                "&version=version" +
                "&appVersion=appVersion" +
                "&includeReferences=includeReferences" +
                "&lon=lon" +
                "&lat=lat" +
                "&lonSpan=lonSpan" +
                "&latSpan=latSpan" +
                "&radius=radius" +
                "&onlyDepartures=onlyDepartures" +
                "&limit=limit" +
                "&minutesBefore=minutesBefore" +
                "&minutesAfter=minutesAfter" +
                "&groupLimit=groupLimit" +
                "&clientLon=clientLon" +
                "&clientLat=clientLat"
    )
    fun getArrivalsAndDeparturesForLocation(
        @Query("key") key: String,
        @Query("lon") lon: Double,
        @Query("lat") lat: Double,
        @Query("clientLon") clientLon: Double,
        @Query("clientLat") clientLat: Double,
    ): Flowable<ColorSpace.Model>

    @GET("stops-for-location.json?" +
            "&version=" +
            "&appVersion=" +
            "&includeReferences=" +
            "&lonSpan=&latSpan=" +
            "&query="
    )
    fun getStopsForLocation(
        @Query("key") key: String,
        @Query("lon") lon: Double,
        @Query("lat") lat: Double,
        @Query("radius") radius : Int
    ): Flowable<StopResponse>

    @GET("stops-for-location.json?")
    fun getStopsForLocation2(
        @Query("key") key: String,
        @Query("lon") lon: Double,
        @Query("lat") lat: Double,
    ): Flowable<ColorSpace.Model>


    @GET("schedule-for-stop.json?" +
                "key=key&version=version&appVersion=appVersion" +
                "&includeReferences=includeReferences" +
                "&stopId=stopId&onlyDepartures=onlyDepartures" +
                "&date=date"
    )
    fun getScheduleForStop(
        @Query("key") key: String,
        @Query("stopID") stopId: String,
    ): Flowable<ColorSpace.Model>

    @GET("trip-details.json?" +
            "key=key&version=version&appVersion=appVersion&includeReferences=includeReferences" +
            "&tripId=tripId" +
            "&vehicleId=vehicleId&date=date")
    fun getTripDetails(
        @Query("key") key: String,
        @Query("tripId") tripId: String,
    )


}
