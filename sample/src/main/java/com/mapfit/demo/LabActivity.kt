package com.mapfit.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.*
import com.mapfit.mapfitdemo.R
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.runBlocking

class LabActivity : AppCompatActivity() {

    lateinit var mapView: MapView
    lateinit var mapfitMap: MapfitMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapfit.getInstance(this, getString(R.string.mapfit_debug_api_key))
        setContentView(R.layout.activity_lab)
        mapView = findViewById(R.id.mapView)
        mapView.getMapAsync(MapTheme.MAPFIT_DAY, object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@LabActivity.mapfitMap = mapfitMap
                testLocation()

            }
        })

    }


    fun produceSquares() = produce<Int> {
        for (x in 1..5) send(x * x)
    }

    fun main(args: Array<String>) = runBlocking<Unit> {
        val squares = produceSquares()
        squares.consumeEach { println(it) }
        println("Done!")
    }

    @SuppressLint("MissingPermission")
    private fun testLocation() {

        mapfitMap.getMapOptions().setUserLocationEnabled(true)
        mapfitMap.getMapOptions().userLocationButtonEnabled = true

        mapfitMap.setZoom(17f)
//
//        val marker = mapfitMap.addMarker(LatLng())
//
//        MapfitLocationProvider(this)
//            .requestLocationUpdates(locationListener = object :
//                LocationListener {
//                override fun onLocation(location: Location) {
//                    marker.setPosition(LatLng(location.latitude, location.longitude))
//                }
//
//                override fun onProviderStatus(availability: ProviderStatus) {
//                    Toast.makeText(
//                        this@LabActivity,
//                        "STATUS CHANGED",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//            })
    }


}
