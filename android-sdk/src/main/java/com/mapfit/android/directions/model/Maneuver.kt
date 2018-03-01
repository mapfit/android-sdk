package com.mapfit.android.directions.model

import com.squareup.moshi.Json

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Maneuver(
    @field:Json(name = "begin_shape_index") val beginShapeIndex: Int,
    @field:Json(name = "travel_mode") val travelMode: String,
    val instruction: String,
    val length: Double,
    @field:Json(name = "street_names") val streetNames: List<String>,
    @field:Json(name = "end_shape_index") val endShapeIndex: Int,
    val time: Int,
    val type: Int,
    @field:Json(name = "verbal_pre_transition_instruction") val verbalPreTransitionInstruction: String,
    @field:Json(name = "travel_type") val travelType: String
)