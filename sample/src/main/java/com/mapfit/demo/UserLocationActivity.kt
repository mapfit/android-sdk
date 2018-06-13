package com.mapfit.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.MapTheme
import com.mapfit.android.MapView
import com.mapfit.android.MapfitMap
import com.mapfit.android.OnMapReadyCallback
import com.mapfit.mapfitdemo.R

class UserLocationActivity : AppCompatActivity() {

    lateinit var mapView: MapView
    lateinit var mapfitMap: MapfitMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)

        mapView.getMapAsync(MapTheme.MAPFIT_DAY, object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                setupMap(mapfitMap)
            }
        })
    }

    private fun setupMap(mapfitMap: MapfitMap) {
        this.mapfitMap = mapfitMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            mapfitMap.getMapOptions().setUserLocationEnabled(true)
        } else {
            // location permission is not granted. You should user for location permission gracefully.
        }

        mapfitMap.getMapOptions().isUserLocationButtonEnabled = true
    }

}
