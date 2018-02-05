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

    var originLocation = LatLng()
    var destinationLocation = LatLng()
    var type: DirectionsType = DirectionsType.DRIVING

    internal var routeDrawn = false


    fun setOrigin(latLng: LatLng): DirectionsOptions {
        this.originLocation = latLng
        return this
    }

    fun setDestination(latLng: LatLng): DirectionsOptions {
        this.destinationLocation = latLng
        return this
    }

    fun setType(type: DirectionsType): DirectionsOptions {
        this.type = type
        return this
    }

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