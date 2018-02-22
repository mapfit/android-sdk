package com.mapfit.android.directions.model

import com.squareup.moshi.Json

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Trip(
    val summary: Summary,
    @field:Json(name = "status_message") val statusMessage: String,
    val legs: List<Leg>,
    val language: String,
    val locations: List<Location>,
    val units: String,
    val status: Int
)
