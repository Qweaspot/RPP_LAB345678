package com.example.laba8

import com.google.gson.annotations.SerializedName

class Address {
    @SerializedName("formatted_address")
    var address: String? = null

    @SerializedName("geometry")
    var geometry: Geometry? = null
}
