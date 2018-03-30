package com.mapfit.android.directions.model

import com.mapfit.android.geometry.LatLngBounds
import com.squareup.moshi.Json

/**
 * Route of a trip from the origin to the destination.
 *
 * Created by dogangulcan on 2/4/18.
 */
data class Route(
    val trip: Trip = Trip(),
    var destinationLocation: List<Double> = listOf(),
    @field:Json(name = "sourceLocation") var originLocation: List<Double> = listOf(),
    @field:Transient var viewport: LatLngBounds = LatLngBounds()
)
