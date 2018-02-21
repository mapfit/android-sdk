package com.mapfit.android.directions.model

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Leg(
    val summary: Summary,
    val shape: String,
    val maneuvers: List<Maneuver>
)
