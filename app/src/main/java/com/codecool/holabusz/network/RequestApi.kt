package com.codecool.holabusz.network

import com.codecool.holabusz.model.DepartureResponse
import com.codecool.holabusz.model.StopResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RequestApi {

    @GET("arrivals-and-departures-for-stop.json?" +
                "&version=3" +
                "&appVersion=" +
                "&includeReferences=false" +
                "&onlyDepartures=true" +
                "&minutesBefore=1" +
                "&minutesAfter=30"
    )
    fun getArrivalsAndDeparturesForStop(
        @Query("key") key: String,
        @Query ("stopId") stopId: String,
        @Query("limit") limit: Int
    ): Single<DepartureResponse>



    @GET("stops-for-location.json?" +
            "&version=3" +
            "&appVersion=" +
            "&includeReferences=" +
            "&lonSpan=&latSpan=" +
            "&query="
    )
    fun getStopsForLocation(
        @Query("key") key: String,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("radius") radius: Int
    ): Single<StopResponse>

    /*

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


     */

}
