package com.codecool.holabusz.network

import android.graphics.ColorSpace
import com.codecool.holabusz.model.Model
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface RequestApi {

    @GET("vehicles-for-location.json?" +
            "key=key" +
            "&version=version" +
            "&appVersion=appVersion" +
            "&includeReferences=includeReferences" +
            "&lon=lon" +
            "&lat=lat" +
            "&latSpan=latSpan" +
            "&lonSpan=lonSpan" +
            "&radius=radius" +
            "&query=query" +
            "&ifModifiedSince=ifModifiedSince")
    fun getVehiclesForLocation(
        @Query("key") key: String,
        @Query("lon") lon: Float,
        @Query("lat") lat: Float
    ) : Flowable<Model>

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
            "&clientLat=clientLat")
    fun getArrivalsAndDeparturesForLocation(
        @Query("key") key: String,
        @Query("lon") lon : Float,
        @Query("lat") lat: Float,
        @Query("clientLon") clientLon : Float,
        @Query("clientLat") clientLat: Float,
    ) : Flowable<Model>

    @GET("stops-for-location.json?" +
            "key=key&version=version" +
            "&appVersion=appVersion" +
            "&includeReferences=includeReferences" +
            "&lon=lon&lat=lat" +
            "&lonSpan=lonSpan" +
            "&latSpan=latSpan" +
            "&radius=radius" +
            "&query=query")
    fun getStopsForLocation(
        @Query("key") key: String,
        @Query("lon") lon : Float,
        @Query("lat") lat: Float,
    ) : Flowable<Model>





}