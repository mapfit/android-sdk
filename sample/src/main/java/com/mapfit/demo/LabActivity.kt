package com.mapfit.demo

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.MapView
import com.mapfit.android.Mapfit
import com.mapfit.android.MapfitMap
import com.mapfit.android.OnMapReadyCallback
import com.mapfit.android.annotations.MapfitMarker
import com.mapfit.android.annotations.Marker
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback
import com.mapfit.android.directions.Directions
import com.mapfit.android.directions.DirectionsCallback
import com.mapfit.android.directions.model.Route
import com.mapfit.android.location.LocationPriority
import com.mapfit.android.location.ProviderStatus
import com.mapfit.android.utils.decodePolyline
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

//        Mapfit.getInstance(this, getString(R.string.mapfit_debug_api_key))
        setContentView(R.layout.activity_lab)
        mapView = findViewById(R.id.mapView)
        mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@LabActivity.mapfitMap = mapfitMap

                testLocation()

                getDirections()
                placeMarkerWithAddress()

                button.setOnClickListener {
                    mapfitMap.getMapOptions()
                        .setUserLocationEnabled(!mapfitMap.getMapOptions().getUserLocationEnabled())
                }

                mapfitMap.getMapOptions()
            }
        })
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
                        val polyline = mapfitMap.addPolyline(leg)

                        mapfitMap.addMarker(
                            "343 gold street brooklyn new york",
                            true,
                            onMarkerAddedCallback = object : OnMarkerAddedCallback {
                                override fun onMarkerAdded(marker: Marker) {
                                }

                                override fun onError(exception: Exception) {
                                }
                            })
                    }
                }

                override fun onError(message: String, e: Exception) {

                }

            })
    }

    private fun placeMarkerWithAddress() {
        val flatironBuildingAddress = "175 5th Ave, New York, NY 10010"
        val withBuildingPolygon = true

        mapfitMap.addMarker(
            flatironBuildingAddress,
            withBuildingPolygon,
            object : OnMarkerAddedCallback {
                override fun onMarkerAdded(marker: Marker) {

                    mapfitMap.setCenter(marker.getPosition())
                    // let's change marker's icon!
                    marker.setIcon(MapfitMarker.EDUCATION)
                    launch {
                        delay(10000)
                        marker.setIcon(MapfitMarker.ARTS)
                    }

                }

                override fun onError(exception: Exception) {
                    // handle the exception
                }
            }
        )
    }

}
