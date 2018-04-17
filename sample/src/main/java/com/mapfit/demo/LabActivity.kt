package com.mapfit.demo

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.MapView
import com.mapfit.android.MapfitMap
import com.mapfit.android.OnMapReadyCallback
import com.mapfit.android.annotations.CapType
import com.mapfit.android.annotations.JoinType
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
import java.util.*


class LabActivity : AppCompatActivity() {

    lateinit var mapView: MapView
    lateinit var mapfitMap: MapfitMap

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab)

        mapView = findViewById(R.id.mapView)
        mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@LabActivity.mapfitMap = mapfitMap

                mapfitMap.setCenter(LatLng(40.744014, -73.990111))
                mapfitMap.setZoom(17f)

//                testLocation()

                button.setOnClickListener {
                    //                    mapfitMap.getMapOptions()
//                        .setUserLocationEnabled(!mapfitMap.getMapOptions().getUserLocationEnabled())
//                    getDirections()
//                    getDirections2()


                    val poly = listOf(
                        LatLng(40.744120, -73.992900),
                        LatLng(40.743502, -73.991667),
                        LatLng(40.744762, -73.990250),
                        LatLng(40.748428, -73.992085),
                        LatLng(40.744120, -73.992900)
                    )


                    val red = mapfitMap.addPolygon(listOf(poly))

                    red.polygonOptions.apply {
                        strokeWidth = 30
                        strokeOutlineWidth = 35
                        strokeColor = "#ff0000"
                        strokeOutlineColor = "#ff0000"
                        fillColor = "#ff0000"
                    }

                    val green = mapfitMap.addPolygon(listOf(poly))

                    green.polygonOptions.apply {
                        strokeWidth = 30
                        strokeOutlineWidth = 35
                        strokeColor = "#00ff00"
                        strokeOutlineColor = "#00ff00"
                        fillColor = "#00ff00"
                    }


                    launch { // g g r g r
                        red.polygonOptions.drawOrder = 400
                        green.polygonOptions.drawOrder = 500

                        delay(3000)
                        red.polygonOptions.drawOrder = 450
                        green.polygonOptions.drawOrder = 500

                        delay(3000)
                        red.polygonOptions.drawOrder = 450
                        green.polygonOptions.drawOrder = 200

                        delay(3000)
                        red.polygonOptions.drawOrder = 450
                        green.polygonOptions.drawOrder = 500

                        delay(3000)
                        red.polygonOptions.drawOrder = 450
                        green.polygonOptions.drawOrder = 100

                        delay(3000)
                        green.polygonOptions.drawOrder = 100


                        delay(3000)
                        red.polygonOptions.drawOrder = 60

//                        repeat(15) {
//                            delay(200)
//                            polygon.polygonOptions.drawOrder = polygon.polygonOptions.drawOrder + 10
//                            polygon2.polygonOptions.drawOrder = polygon2.polygonOptions.drawOrder -
//                                    10
//                        }

                    }


                    val line = listOf(
                        LatLng(40.744120, -73.992900),
                        LatLng(40.743502, -73.991667),
                        LatLng(40.744762, -73.990250)
                    )

                    val polyline = mapfitMap.addPolyline(line)

                    polyline.polylineOptions.apply {
                        strokeWidth = 3
                        strokeOutlineWidth = 8
                        strokeColor = "#4353ff"
                        strokeOutlineColor = "#4c4353ff"
                        lineJoinType = JoinType.ROUND
                        lineCapType = CapType.ROUND
                    }
                    launch {

                        polyline.polylineOptions.apply {
                            strokeWidth = 20
                            strokeOutlineWidth = 20

                            strokeColor = "#00FF00"
                            strokeOutlineColor = "#123456"
                        }
                        }
//
//                        polyline.polylineOptions.strokeOutlineColor = "#545412fa"
//                        polygon.polygonOptions.strokeOutlineColor = "#545412fa"
//
//                    }
//                    launch {
//                        delay(4000)
//                        polyline.polylineOptions.strokeWidth = 20
//                        polyline.polylineOptions.strokeOutlineWidth = 20
//                        polyline.polylineOptions.strokeOutlineColor = "#123456"
//
//                    }
//
//
//                    launch {
//                        repeat(15) {
//                            delay(1000)
//                            polygon.polygonOptions.strokeWidth = it * 15
//                            polyline.polylineOptions.strokeWidth = it * 15
//                        }
//                    }


                }
//                placeMarkerWithAddress()

                button.callOnClick()
                mapfitMap.getMapOptions()
            }
        })
    }


    /**
     * Adds a polygon and styles it.
     */
    internal fun addStyledPolygon() {
        val poly = mutableListOf<LatLng>()
        poly.add(LatLng(40.744120, -73.992900))
        poly.add(LatLng(40.743502, -73.991667))
        poly.add(LatLng(40.744762, -73.990250))
        poly.add(LatLng(40.748428, -73.992085))
        poly.add(LatLng(40.744120, -73.992900))

        val polyRings = ArrayList<List<LatLng>>()
        polyRings.add(poly)

        val polygon = mapfitMap.addPolygon(polyRings)

        polygon.polygonOptions.apply {
            fillColor = "#22ff2200"
            strokeWidth = 15
            strokeOutlineWidth = 15
            strokeColor = "#5400eaea"
            lineJoinType = JoinType.ROUND
            strokeOutlineColor = "#54FF0000"
        }
    }

    /**
     * Adds a polygon and styles it.
     */
    internal fun addStyledPolyline() {
        val line = mutableListOf<LatLng>()
        line.add(LatLng(40.744120, -73.992900))
        line.add(LatLng(40.743502, -73.991667))
        line.add(LatLng(40.744762, -73.990250))
        line.add(LatLng(40.748428, -73.992085))


        val polyline = mapfitMap.addPolyline(line)

        polyline.polylineOptions.apply {
            strokeWidth = 15
            strokeOutlineWidth = 15
            lineCapType = CapType.ROUND
            lineJoinType = JoinType.ROUND
            strokeColor = "#5400eaea"
            strokeOutlineColor = "#54FF0000"
        }
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

                        polyline.polylineOptions.apply {
                            strokeColor = "#54ff0000"
                            strokeWidth = 20
                        }

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

                        polyline.polylineOptions.apply {
                            strokeColor = "#540000ff"
                            strokeWidth = 20
                        }

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
                    marker.buildingPolygon?.polygonOptions?.apply {
                        strokeWidth = 20
                        strokeOutlineWidth = 25
                        strokeColor = "#5400ff00"
                        strokeOutlineColor = "#540000ff"
                        lineJoinType = JoinType.ROUND
                        fillColor = "#54ff0000"
                    }

                    mapfitMap.setCenter(marker.getPosition())
                    mapfitMap.setZoom(18f)
                }

                override fun onError(exception: Exception) {
                    if (exception != null) {

                    }
                }
            }
        )
    }

}
