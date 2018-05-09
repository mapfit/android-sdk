package com.mapfit.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.MapView
import com.mapfit.android.MapfitMap
import com.mapfit.android.OnMapReadyCallback
import com.mapfit.android.annotations.CapType
import com.mapfit.android.annotations.JoinType
import com.mapfit.android.annotations.Marker
import com.mapfit.android.annotations.MarkerOptions
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback
import com.mapfit.android.directions.Directions
import com.mapfit.android.directions.DirectionsCallback
import com.mapfit.android.directions.model.Route
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.utils.decodePolyline
import com.mapfit.mapfitdemo.R

/**
 * This activity is to demonstrate styling polygon and polyline.
 *
 */
class PolyStylingActivity : AppCompatActivity() {

    lateinit var mapView: MapView
    lateinit var mapfitMap: MapfitMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {

                setupMap(mapfitMap)

            }
        })
    }

    fun setupMap(mapfitMap: MapfitMap) {
        this.mapfitMap = mapfitMap

        mapfitMap.setCenter(LatLng(40.744014, -73.990111))
        mapfitMap.setZoom(15f)

        drawDirections()
        placeMarkerWithAddress()
        addStyledPolygon()
    }

    /**
     * Adds a polygon and styles it.
     */
    fun addStyledPolygon() {
        val poly = listOf(
            LatLng(40.744120, -73.992900),
            LatLng(40.743502, -73.991667),
            LatLng(40.744762, -73.990250),
            LatLng(40.745875, -73.991823),
            LatLng(40.744120, -73.992900)
        )

        val polygon = mapfitMap.addPolygon(listOf(poly))

        polygon!!.polygonOptions.apply {
            strokeWidth = 3
            strokeOutlineWidth = 8
            strokeColor = "#32b3ff"
            strokeOutlineColor = "#5932b3ff"
            lineJoinType = JoinType.ROUND
            fillColor = "#2732b3ff"
        }
    }

    /**
     * Draws a styled route that is obtained from Mapfit Directions API.
     */
    fun drawDirections() {
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

                        polyline!!.polylineOptions.apply {
                            strokeColor = "#ffabff9e"
                            strokeWidth = 20
                            lineCapType = CapType.ROUND
                        }
                    }
                }

                override fun onError(message: String, e: Exception) {}

            })
    }

    /**
     * Adding a marker with building polygon on the map.
     */
    fun placeMarkerWithAddress() {
        val flatironBuildingAddress = "175 5th Ave, New York, NY 10010"

        val markerOptions = MarkerOptions()
            .streetAddress(flatironBuildingAddress)
            .addBuildingPolygon(true)

        mapfitMap.addMarker(
            markerOptions,
            object : OnMarkerAddedCallback {
                override fun onMarkerAdded(marker: Marker) {

                    // here we style the building polygon if there is one
                    marker.buildingPolygon?.polygonOptions?.apply {
                        strokeWidth = 20
                        strokeOutlineWidth = 25
                        strokeColor = "#5400ff00"
                        strokeOutlineColor = "#540000ff"
                        fillColor = "#54ff0000"
                        lineJoinType = JoinType.ROUND
                    }

                }

                override fun onError(exception: Exception) {}
            }
        )
    }

}
