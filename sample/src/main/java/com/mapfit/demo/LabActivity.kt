package com.mapfit.demo

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.mapfit.android.MapView
import com.mapfit.android.MapfitMap
import com.mapfit.android.OnMapReadyCallback
import com.mapfit.android.OnMapThemeLoadListener
import com.mapfit.android.annotations.*
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback
import com.mapfit.android.annotations.callback.OnPolygonClickListener
import com.mapfit.android.annotations.callback.OnPolylineClickListener
import com.mapfit.android.directions.Directions
import com.mapfit.android.directions.DirectionsCallback
import com.mapfit.android.directions.model.Route
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.location.LocationPriority
import com.mapfit.android.location.ProviderStatus
import com.mapfit.android.utils.decodePolyline
import com.mapfit.mapfitdemo.R
import com.mapfit.tetragon.SceneUpdate
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.*


class LabActivity : AppCompatActivity() {

    lateinit var mapView: MapView
    lateinit var mapfitMap: MapfitMap


    val latLng = LatLng(40.730252, -73.999524)

    val poly = listOf(
        listOf(
            LatLng(40.744120, -73.992900),
            LatLng(40.743502, -73.991667),
            LatLng(40.744762, -73.990250),
            LatLng(40.748428, -73.992085),
            LatLng(40.744120, -73.992900)
        )
    )

    val line = listOf(
        LatLng(40.744120, -73.992900),
        LatLng(40.743502, -73.991667),
        LatLng(40.744762, -73.990250)
    )

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

                mapfitMap.setOnMapThemeLoadListener(object : OnMapThemeLoadListener {
                    override fun onLoaded() {
                    }

                    override fun onError() {
                    }
                })


                mapfitMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .icon("https://cdn.britannica.com/700x450/91/1291-004-8FED0EE7.jpg")
                )

                launch {
                    delay(4000)

                    val sceneUpdate = SceneUpdate("global.show_transit", "true")
                    mapfitMap.getMapOptions().updateScene(listOf(sceneUpdate))
                }

                mapfitMap.addPolygon(
                    PolygonOptions()
                        .points(poly)
                        .fillColor("#22ff2200")
                        .strokeWidth(15)
                        .strokeColor("#5400eaea")
                        .lineJoinType(JoinType.ROUND)
                        .strokeOutlineColor("#54FF0000")
                        .strokeOutlineWidth(8)
                )


                mapfitMap.addPolyline(
                    PolylineOptions()
                        .points(line)
                        .strokeColor("#000000")
                        .strokeWidth(30)
                        .drawOrder(1000)
                )

                mapfitMap.setOnPolygonClickListener(object : OnPolygonClickListener {
                    override fun onPolygonClicked(polygon: Polygon) {
                        Toast.makeText(
                            this@LabActivity,
                            "Polygon is Clicked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

                mapfitMap.setOnPolylineClickListener(object : OnPolylineClickListener {
                    override fun onPolylineClicked(polyline: Polyline) {
                        Toast.makeText(
                            this@LabActivity,
                            "Polyline is Clicked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })


//                mapfitMap.getMapOptions().cameraType = MapOptions.CameraType.ISOMETRIC

//                getDirections()
//                placeMarkerWithAddress()
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

//        val polygon = mapfitMap.addPolygon(polyRings)

//        polygon!!.polygonOptions.apply {
//            fillColor = "#22ff2200"
//            strokeWidth = 15
//            strokeOutlineWidth = 15
//            strokeColor = "#5400eaea"
//            lineJoinType = JoinType.ROUND
//            strokeOutlineColor = "#54FF0000"
//        }
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


//        val polyline = mapfitMap.addPolyline(line)

//        polyline?.polylineOptions?.apply {
//            strokeWidth = 15
//            strokeOutlineWidth = 15
//            lineCapType = CapType.ROUND
//            lineJoinType = JoinType.ROUND
//            strokeColor = "#5400eaea"
//            strokeOutlineColor = "#54FF0000"
//        }
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


                        mapfitMap.addMarker(MarkerOptions()
                            .streetAddress("343 gold street brooklyn new york", true),
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
                        val polyline = mapfitMap.addPolyline(PolylineOptions().points(leg))

//                        polyline?.polylineOptions?.apply {
//                            strokeColor = "#540000ff"
//                            strokeWidth = 20
//                        }

                        mapfitMap.addMarker(MarkerOptions().streetAddress(
                            "343 gold street brooklyn new york",
                            true
                        ),
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

        val markerOptions = MarkerOptions()
            .streetAddress(flatironBuildingAddress, true)
            .addBuildingPolygon(true)
            .icon(MapfitMarker.SPORTS)


        val marker = mapfitMap.addMarker(
            markerOptions,
            object : OnMarkerAddedCallback {
                override fun onMarkerAdded(marker: Marker) {

                    mapfitMap.setCenter(marker.position)
                    mapfitMap.setZoom(18f)
                }

                override fun onError(exception: Exception) {
                    exception.printStackTrace()
                }
            }
        )


    }

}
