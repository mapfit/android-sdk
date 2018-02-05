package com.mapfit.mapfitsdk.directions.model

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Maneuver(
    val begin_shape_index: Int,
    val travel_mode: String,
    val instruction: String,
    val length: Double,
    val street_names: List<String>,
    val end_shape_index: Int,
    val time: Int,
    val type: Int,
    val verbal_pre_transition_instruction: String,
    val travel_type: String
)