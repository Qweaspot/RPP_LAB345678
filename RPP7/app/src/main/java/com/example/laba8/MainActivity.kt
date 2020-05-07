package com.example.laba8

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View

import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity(), PageFragment.OnFragmentDataListener {
    private var pager: ViewPager? = null

    private var lat1: Double? = 0.0
    private var lng1: Double? = 0.0
    private var lat2: Double? = 0.0
    private var lng2: Double? = 0.0
    private var myPositionLat: Double? = null
    private var myPositionLng: Double? = null
    private var flag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getLocationPermission()

        pager = findViewById(R.id.pager)
        pager!!.adapter = ViewPagerAdapter(supportFragmentManager)
    }

    override fun onFragmentDataListener(lat: Double?, lng: Double?) {
        if (pager!!.currentItem == Constants.PAGE_FROM) {
            lat1 = lat
            lng1 = lng
        }
        if (pager!!.currentItem == Constants.PAGE_TO) {
            lat2 = lat
            lng2 = lng
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            flag = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        flag = false
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                flag = true
                setGpsLocation()
            }
        }
    }

    private fun setGpsLocation() {
        try {
            if (flag) {
                val locationResult = LocationServices
                    .getFusedLocationProviderClient(this)
                    .lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        myPositionLat = task.result!!.latitude
                        myPositionLng = task.result!!.longitude
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: ", e.message)
        }

    }

    fun onShowPathClick(view: View) {
        if (lat1 != 0.0 && lng1 != 0.0 && lat2 != 0.0 && lng2 != 0.0) {
            val intent = Intent(this, PathActivity::class.java)
            intent.putExtra(Constants.LAT1_KEY, lat1)
            intent.putExtra(Constants.LNG1_KEY, lng1)
            intent.putExtra(Constants.LAT2_KEY, lat2)
            intent.putExtra(Constants.LNG2_KEY, lng2)
            intent.putExtra(Constants.MY_POSITION_LAT_KEY, myPositionLat)
            intent.putExtra(Constants.MY_POSITION_LNG_KEY, myPositionLng)
            startActivity(intent)
        }
    }
}
