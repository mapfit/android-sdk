package com.mapfit.android.directions.model

/**
 * Route of a trip from the origin to the destination.
 *
 * Created by dogangulcan on 2/4/18.
 */
data class Route(
    val trip: Trip,
    var destinationLocation: List<Double>,
    var sourceLocation: List<Double>
)
