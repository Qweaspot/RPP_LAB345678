package com.example.laba8

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

import com.google.maps.android.PolyUtil
import java.util.ArrayList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PathActivity : AppCompatActivity(), OnMapReadyCallback {

    private var lat1: Double? = null
    private var lng1: Double? = null
    private var lat2: Double? = null
    private var lng2: Double? = null
    private val places = ArrayList<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        val arguments = intent.extras
        lat1 = arguments!!.getDouble(Constants.LAT1_KEY)
        lng1 = arguments.getDouble(Constants.LNG1_KEY)
        lat2 = arguments.getDouble(Constants.LAT2_KEY)
        lng2 = arguments.getDouble(Constants.LNG2_KEY)

        places.add(LatLng(lat1!!, lng1!!))
        places.add(LatLng(lat2!!, lng2!!))
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        setMarkers(googleMap)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (googleMap != null) {
                googleMap.isMyLocationEnabled = true
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val directionsApi = retrofit.create(DirectionsAPI::class.java)
        val routeResponseCall = directionsApi.getRoute(
            lat1.toString() + "," + lng1,
            lat2.toString() + "," + lng2, Constants.MAPS_KEY
        )

        routeResponseCall.enqueue(object : Callback<RouteResponse> {
            override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                if (response.body()!!.status == Constants.STATUS_OK) {
                    val mPoints = PolyUtil.decode(response.body()!!.points!!)

                    val line = PolylineOptions()
                    line.width(4f).color(R.color.colorPrimaryDark)

                    val latLngBuilder = LatLngBounds.Builder()
                    for (i in mPoints.indices) {

                        line.add(mPoints[i])
                        latLngBuilder.include(mPoints[i])
                    }
                    googleMap!!.addPolyline(line)

                    val size = resources.displayMetrics.widthPixels
                    val latLngBounds = latLngBuilder.build()
                    val track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25)
                    googleMap.moveCamera(track)
                } else {
                    val textView = findViewById<TextView>(R.id.text)
                    textView.setText(R.string.result)
                }
            }

            override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                Log.e(TAG, "Error! $t")
            }
        })
    }

    private fun setMarkers(googleMap: GoogleMap?) {
        val markers = arrayOfNulls<MarkerOptions>(places.size)
        for (i in places.indices) {
            markers[i] = MarkerOptions().position(places[i])
            googleMap!!.addMarker(markers[i])
        }
    }

    companion object {
        private val TAG = PathActivity::class.java.simpleName
    }
}
