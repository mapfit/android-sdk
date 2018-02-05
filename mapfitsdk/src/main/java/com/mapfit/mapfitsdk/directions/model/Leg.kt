package com.mapfit.mapfitsdk.directions.model

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Leg(
    val summary: Summary,
    val shape: String,
    val maneuvers: List<Maneuver>
)
