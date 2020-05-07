package com.example.laba8

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingAPI {
    @GET("place/textsearch/json?")
    fun getAddress(
        @Query("query") address: String,
        @Query("key") key: String
    ): Call<GeocodingResponse>
}
