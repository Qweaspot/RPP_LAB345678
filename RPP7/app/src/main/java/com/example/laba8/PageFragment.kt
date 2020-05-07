package com.example.laba8

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import java.io.IOException
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PageFragment : Fragment(), OnMapReadyCallback {
    private var pageNumber: Int = 0

    private val placeWithCoords = HashMap<String, ArrayList<Double>>()
    private val placesList = ArrayList<String>()

    private var listView: ListView? = null

    private var map: GoogleMap? = null
    private var geocodingApi: GeocodingAPI? = null

    private var mListener: OnFragmentDataListener? = null

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    interface OnFragmentDataListener {
        fun onFragmentDataListener(lat: Double?, lng: Double?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentDataListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNumber = if (arguments != null) arguments!!.getInt(Constants.PAGE_KEY) else 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        geocodingApi = retrofit.create(GeocodingAPI::class.java)

        val editText = view.findViewById<EditText>(R.id.edit_text)
        val button = view.findViewById<Button>(R.id.button)

        listView = view.findViewById(R.id.list)

        button.setOnClickListener {
            val inputAddress = editText.text.toString()

            if (inputAddress.length != 0) {
                ProcessTask(inputAddress).execute()
            }
        }

        listView!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val place = placesList[position]
                val lat = placeWithCoords[place]!![0]
                val lng = placeWithCoords[place]!![1]

                mListener!!.onFragmentDataListener(lat, lng)

                val marker = LatLng(lat, lng)
                map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))
                map!!.addMarker(MarkerOptions().title("You here!").position(marker))
            }

    }

    private inner class ProcessTask(private val input: String) : AsyncTask<Void, Void, Void>() {
        private var response: Response<GeocodingResponse>? = null

        override fun doInBackground(vararg arg0: Void): Void? {
            try {
                response = geocodingApi!!.getAddress(input, Constants.MAPS_KEY).execute()
            } catch (ex: IOException) {
                Log.e(TAG, "" + ex.message)
            }

            return null
        }

        override fun onPostExecute(result: Void) {
            placesList.clear()
            for (i in response!!.body()!!.addressList!!) {
                val lat = i.geometry!!.coordinate!!.lat
                val lng = i.geometry!!.coordinate!!.lng
                placeWithCoords[i.address] = ArrayList(Arrays.asList(lat, lng))
                placesList.add(i.address)
            }
            val adapter = ArrayAdapter(
                activity!!,
                android.R.layout.simple_list_item_1, placesList
            )
            listView!!.adapter = adapter
        }
    }

    companion object {

        private val TAG = PageFragment::class.java.simpleName

        fun newInstance(page: Int): PageFragment {
            val fragment = PageFragment()
            val args = Bundle()
            args.putInt(Constants.PAGE_KEY, page)

            fragment.arguments = args
            return fragment
        }

        internal fun getTitle(position: Int): String {
            return if (position == Constants.PAGE_FROM) "Откуда" else "Куда"
        }
    }
}
