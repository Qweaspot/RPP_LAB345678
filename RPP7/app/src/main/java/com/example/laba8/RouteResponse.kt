package com.example.laba8

import com.google.gson.annotations.SerializedName

class RouteResponse {

    @SerializedName("routes")
    var routes: List<Route>? = null

    val points: String?
        get() = this.routes!![0].overview_polyline!!.points

    @SerializedName("status")
    var status: String? = null

    internal inner class Route {
        var overview_polyline: OverviewPolyline? = null
    }

    internal inner class OverviewPolyline {
        var points: String? = null
    }
}
