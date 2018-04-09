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
import com.mapfit.android.geometry.LatLng
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

        Mapfit.getInstance(this, getString(R.string.mapfit_debug_api_key))
        setContentView(R.layout.activity_lab)
        mapView = findViewById(R.id.mapView)
        mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@LabActivity.mapfitMap = mapfitMap

                mapfitMap.setCenter(LatLng(40.744014, -73.990111))
                mapfitMap.setZoom(15f)


//                testLocation()


                button.setOnClickListener {
                    //                    mapfitMap.getMapOptions()
//                        .setUserLocationEnabled(!mapfitMap.getMapOptions().getUserLocationEnabled())
//                    getDirections()
//                    getDirections2()
////
//                    val poly = listOf(
//                        LatLng(40.744120, -73.992900),
//                        LatLng(40.743502, -73.991667),
//                        LatLng(40.744762, -73.990250),
//                        LatLng(40.748428, -73.992085),
//                        LatLng(40.744120, -73.992900)
//                    )
//                    val line = listOf(LatLng(40.742729, -73.994500), LatLng(40.741546, -73.991747))
//
//                    val polygon = mapfitMap.addPolygon(listOf(poly))
//                    val polyline = mapfitMap.addPolyline(line)
//
//                    launch {
//
//                        polygon.polygonOptions.apply {
//                            strokeWidth = 30
//                            strokeOutlineWidth = 35
//
//                            strokeColor = "#00eaea"
//                            lineCapType = CapType.ROUND
//                            lineJoinType = JoinType.ROUND
//                            strokeOutlineColor = "#FF0000"
//                            fillColor = "#22ff2200"
//                        }
//
//                        polyline.polylineOptions.apply {
//                            strokeWidth = 20
//                            strokeOutlineWidth = 20
//                            lineCapType = CapType.ROUND
//
//                            strokeColor = "#00FF00"
//                            strokeOutlineColor = "#123456"
//                        }

//                        delay(3000)
//                        polyline.polylineOptions.lineCapType = CapType.MITER
//                        polygon.polygonOptions.lineCapType = CapType.MITER
//
//                        delay(3000)
//                        polyline.polylineOptions.strokeOutlineColor = "#5412fa"
//                        polygon.polygonOptions.strokeOutlineColor = "#5412fa"
//
//                        delay(10000)
//                        polyline.remove()
//                        polygon.remove()
//                    }
//                    launch {
//                        delay(4000)
//                        polyline.polylineOptions.strokeWidth = 20
//                        polyline.polylineOptions.strokeOutlineWidth = 20
//                        polyline.polylineOptions.strokeOutlineColor = "#123456"
//
//                    }


//                    launch {
//                        repeat(15) {
//                            delay(1000)
//                            polygon.polygonOptions.strokeWidth = it * 15
//                            polyline.polylineOptions.strokeWidth = it * 15
//                        }
//                    }


                }
                placeMarkerWithAddress()

                button.callOnClick()
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

        mapfitMap.setZoom(15f)

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
        val destinationAddress = "1019-999 6th Ave, New York, NY 10018"

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

    private fun getDirections2() {
        val originAddress = "175 5th Ave, New York, NY 10010"
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
//                    marker.setIcon(MapfitMarker.EDUCATION)


                    marker.buildingPolygon?.polygonOptions?.apply {
                        strokeWidth = 15
                        strokeOutlineWidth = 10


                        fillColor = "#54ff0000"

                        strokeColor = "#00eaea"

                        strokeOutlineColor = "#00FF56"
                    }

                    mapfitMap.setCenter(marker.getPosition())
                    // let's change marker's icon!
                    launch {
                        delay(10000)
                        marker.setIcon(MapfitMarker.ARTS)
//                        mapfitMap.getMapOptions().theme = MapTheme.MAPFIT_GRAYSCALE
                    }


                }

                override fun onError(exception: Exception) {
                    // handle the exception
                }
            }
        )
    }

}
