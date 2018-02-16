package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.annotations.Polyline
import com.mapfit.mapfitsdk.directions.Directions
import com.mapfit.mapfitsdk.directions.DirectionsCallback
import com.mapfit.mapfitsdk.directions.DirectionsType
import com.mapfit.mapfitsdk.directions.model.Route
import com.mapfit.mapfitsdk.geometry.LatLng
import com.mapfit.mapfitsdk.utils.decodePolyline

/**
 * Used to obtain route and draw directions on map from an origin to a destination.
 *
 * Created by dogangulcan on 1/4/18.
 */
class DirectionsOptions internal constructor(private val mapController: MapController) {

    private var originLocation = LatLng()
    private var destinationLocation = LatLng()
    private var originLocationString = ""
    private var destinationLocationString = ""
    private var type: DirectionsType = DirectionsType.DRIVING

    internal var routeDrawn = false

    /**
     * Sets origin for the directions to the given [LatLng] coordinates.
     *
     * @param latLng coordinates for origin
     */
    fun setOrigin(latLng: LatLng): DirectionsOptions {
        this.originLocation = latLng
        return this
    }

    /**
     * Sets destination for the directions to the given [LatLng] coordinates.
     *
     * @param latLng coordinates for destination
     */
    fun setDestination(latLng: LatLng): DirectionsOptions {
        this.destinationLocation = latLng
        return this
    }

    /**
     * Sets origin for the directions to the given street address.
     *
     * @param address street address for origin
     */
    fun setOrigin(address: String): DirectionsOptions {
        this.originLocationString = address
        return this
    }

    /**
     * Sets destination for the directions to the given street address.
     *
     * @param address street address for destination
     */
    fun setDestination(address: String): DirectionsOptions {
        this.destinationLocationString = address
        return this
    }

    /**
     * Sets type for the directions. See [DirectionsType].
     *
     * @param type of directions
     */
    fun setType(type: DirectionsType): DirectionsOptions {
        this.type = type
        return this
    }

    /**
     * Returns the current [DirectionsType].
     * @return directions type
     */
    fun getType() = type

    /**
     * Draws the route as polyline on the map and returns the route details to [RouteDrawCallback].
     *
     * @param callback will be called when the route is drawn on the map as polyline
     */
    fun showDirections(callback: RouteDrawCallback) {

        val directionsCallback = object : DirectionsCallback {
            override fun onSuccess(route: Route) {
                val legs = drawRoute(route)
                callback.onRouteDrawn(route, legs)
            }

            override fun onError(message: String, e: Exception) {
                callback.onError("", e)
            }
        }
        Directions().getRoute(
            originLocation = originLocation,
            destinationLocation = destinationLocation,
            directionsType = type,
            callback = directionsCallback
        )
    }

    /**
     * Callback to be invoked when the route is drawn or there is an error.
     */
    interface RouteDrawCallback {

        fun onRouteDrawn(route: Route, legs: List<Polyline>)

        /**
         * Called when route is not drawn and an error has occurred.
         */
        fun onError(message: String, e: Exception)

    }

    private fun drawRoute(route: Route): List<Polyline> {
        val legs = mutableListOf<Polyline>()
        route.trip.legs.forEach {
            val line = decodePolyline(it.shape)
            val polyline = mapController.addPolyline(line)
            legs.add(polyline)
            routeDrawn = true
        }
        return legs
    }

}