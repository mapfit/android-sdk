package com.mapfit.demo

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.MapView
import com.mapfit.android.Mapfit
import com.mapfit.android.MapfitMap
import com.mapfit.android.OnMapReadyCallback
import com.mapfit.android.annotations.Anchor
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.location.LocationPriority
import com.mapfit.android.location.ProviderStatus
import com.mapfit.mapfitdemo.R
import kotlinx.android.synthetic.main.activity_lab.*


class LabActivity : AppCompatActivity() {

    lateinit var mapView: MapView
    lateinit var mapfitMap: MapfitMap

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapfit.getInstance(this, getString(R.string.mapfit_debug_api_key))
        setContentView(R.layout.activity_lab)
        mapView = findViewById(R.id.mapView)
        mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@LabActivity.mapfitMap = mapfitMap

                testLocation()

                placeMarker()

                button.setOnClickListener {
                    mapfitMap.getMapOptions()
                        .setUserLocationEnabled(!mapfitMap.getMapOptions().getUserLocationEnabled())
                }

                mapfitMap.getMapOptions()
            }
        })
    }

    private fun placeMarker() {
        val position = LatLng(40.744023, -73.993150)
        val marker = mapfitMap.addMarker(position)
        marker.markerOptions.flat = true
        marker.markerOptions.anchor= Anchor.BOTTOM
        marker.markerOptions.flat = false
        marker.markerOptions.anchor= Anchor.BOTTOM_RIGHT

    }

    @SuppressLint("MissingPermission")
    private fun testLocation() {

        mapfitMap.getMapOptions().setUserLocationEnabled(
            enable = true,
            locationPriority = LocationPriority.HIGH_ACCURACY,
            listener = object : com.mapfit.android.location.LocationListener {
                override fun onLocation(location: Location) {
                }

                override fun onProviderStatus(availability: ProviderStatus) {
                }

            })

        mapfitMap.getMapOptions().userLocationButtonEnabled = true

        mapfitMap.setZoom(17f)

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
