package com.mapfit.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.*
import com.mapfit.android.annotations.MapfitMarker
import com.mapfit.android.annotations.Marker
import com.mapfit.android.annotations.MarkerOptions
import com.mapfit.android.annotations.PolylineOptions
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback
import com.mapfit.android.directions.Directions
import com.mapfit.android.directions.DirectionsCallback
import com.mapfit.android.directions.model.Route
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.utils.decodePolyline
import com.mapfit.mapfitdemo.R

class KotlinActivity : AppCompatActivity() {

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

        mapfitMap.setCenter(LatLng(40.74405, -73.99324))
        mapfitMap.setZoom(14f)
        mapfitMap.getMapOptions().isUserLocationButtonVisible = true

        placeMarker()
        placeMarkerWithAddress()
        getDirections()
        setEventListeners()

        // enable ui controls
        mapfitMap.getMapOptions().isRecenterButtonVisible = true
        mapfitMap.getMapOptions().isZoomControlVisible = true
        mapfitMap.getMapOptions().isCompassButtonVisible = true
    }

    private fun setEventListeners() {
        mapfitMap.setOnMapClickListener(object : OnMapClickListener {
            override fun onMapClicked(latLng: LatLng) {
                // map is clicked!
            }
        })

        mapfitMap.setOnMapDoubleClickListener(object : OnMapDoubleClickListener {
            override fun onMapDoubleClicked(latLng: LatLng) {
                // map is double clicked!
            }
        })

    }

    private fun placeMarker() {
        val position = LatLng(40.744023, -73.993150)
        val marker = mapfitMap.addMarker(MarkerOptions().position(position))
    }

    private fun getDirections() {
        val originAddress = "111 Macdougal Street new york ny"
        val destinationAddress = "119 W 24th Street new york ny"

        Directions().route(
            originAddress,
            destinationAddress,
            callback = object : DirectionsCallback {

                override fun onSuccess(route: Route) {
                    route.trip.legs.forEach {
                        val leg = decodePolyline(it.shape)
                        val polyline = mapfitMap.addPolyline(PolylineOptions().points(leg))
                    }
                }

                override fun onError(message: String, e: Exception) {

                }

            })
    }

    private fun placeMarkerWithAddress() {
        val flatironBuildingAddress = "175 5th Ave, New York, NY 10010"

        val markerOptions = MarkerOptions()
            .streetAddress(flatironBuildingAddress)
            .addBuildingPolygon(true)
            .icon(MapfitMarker.EDUCATION)

        mapfitMap.addMarker(
            markerOptions,
            object : OnMarkerAddedCallback {
                override fun onMarkerAdded(marker: Marker) {
                    // marker is added to the map!
                }

                override fun onError(exception: Exception) {
                    // handle the exception
                }
            }
        )
    }

}
