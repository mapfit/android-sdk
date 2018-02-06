package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.directions.DirectionsApi
import com.mapfit.mapfitsdk.directions.DirectionsCallback
import com.mapfit.mapfitsdk.directions.DirectionsType
import com.mapfit.mapfitsdk.directions.model.Route
import com.mapfit.mapfitsdk.geometry.LatLng
import com.mapfit.mapfitsdk.utils.decodePolyline

/**
 * Created by dogangulcan on 1/4/18.
 */
class DirectionsOptions(private val mapController: MapController) {

    private var originLocation = LatLng()
    private var destinationLocation = LatLng()
    private var originLocationString = ""
    private var destinationLocationString = ""
    var type: DirectionsType = DirectionsType.DRIVING

    internal var routeDrawn = false

    /**
     * @param latLng coordinates for origin
     */
    fun setOrigin(latLng: LatLng): DirectionsOptions {
        this.originLocation = latLng
        return this
    }

    /**
     * @param latLng coordinates for destination
     */
    fun setDestination(latLng: LatLng): DirectionsOptions {
        this.destinationLocation = latLng
        return this
    }

    /**
     * @param address street address for origin
     */
    fun setOrigin(address: String): DirectionsOptions {
        this.originLocationString = address
        return this
    }

    /**
     * @param address street address for destination
     */
    fun setDestination(address: String): DirectionsOptions {
        this.destinationLocationString = address
        return this
    }

    fun setType(type: DirectionsType): DirectionsOptions {
        this.type = type
        return this
    }

    /**
     * Displays the route as polyline on the map and returns
     *
     * @param callback will be called when the route is drawn on the map as polyline
     */
    fun showDirections(callback: RouteDrawCallback) {

        val directionsCallback = object : DirectionsCallback {
            override fun onSuccess(route: Route) {
                drawRoute(route)
                callback.onRouteDrawn(route)
            }

            override fun onError(message: String, e: Exception) {
                callback.onError("", e)
            }
        }
        DirectionsApi().getDirections(
            originLocation = originLocation,
            destinationLocation = destinationLocation,
            directionsType = type,
            callback = directionsCallback
        )
    }

    interface RouteDrawCallback {

        fun onRouteDrawn(route: Route)

        fun onError(message: String, e: Exception)

    }

    private fun drawRoute(route: Route) {
        route.trip.legs.forEach {
            val line = decodePolyline(it.shape)
            mapController.addPolyline(line)
            routeDrawn = true
        }
    }

}