package com.example.laba8

import com.google.gson.annotations.SerializedName

class GeocodingResponse {
    @SerializedName("results")
    var addressList: List<Address>? = null
}
