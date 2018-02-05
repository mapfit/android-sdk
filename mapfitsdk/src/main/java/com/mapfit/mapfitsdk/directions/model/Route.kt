package com.mapfit.mapfitsdk.directions.model

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Route(
		val trip: Trip,
		val destinationLocation: List<Double>,
		val sourceLocation: List<Double>
)
