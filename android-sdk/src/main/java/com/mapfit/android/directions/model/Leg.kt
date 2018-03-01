package com.mapfit.android.directions.model

/**
 * An individual step within the set of directions returned by the Directions API.
 *
 * Created by dogangulcan on 2/4/18.
 */
data class Leg(
    val summary: Summary,
    val shape: String,
    val maneuvers: List<Maneuver>
)
