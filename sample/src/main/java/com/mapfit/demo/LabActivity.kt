package com.mapfit.demo

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.*
import com.mapfit.android.annotations.Marker
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.location.ProviderStatus
import com.mapfit.mapfitdemo.R
import kotlinx.android.synthetic.main.activity_lab.*
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch


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

                launch {
                    delay(1300)
                    mapfitMap.getMapOptions().theme = MapTheme.MAPFIT_NIGHT

                }
                button.setOnClickListener {
                    mapfitMap.getMapOptions()
                        .setUserLocationEnabled(!mapfitMap.getMapOptions().getUserLocationEnabled())
                }


            }
        })


    }

    @SuppressLint("MissingPermission")
    private fun testLocation() {

        mapfitMap.getMapOptions().setUserLocationEnabled(
            true,
            listener = object : com.mapfit.android.location.LocationListener {
                override fun onLocation(location: Location) {
                    mapfitMap.setCenter(LatLng(location.latitude, location.longitude), 200)
                }

                override fun onProviderStatus(availability: ProviderStatus) {
                }

            })
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
