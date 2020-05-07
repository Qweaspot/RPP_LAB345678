package com.example.laba8

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsAPI {
    @GET("directions/json?")
    fun getRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String
    ): Call<RouteResponse>
}
