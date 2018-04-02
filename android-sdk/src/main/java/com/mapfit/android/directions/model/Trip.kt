package com.mapfit.android.directions.model

import com.squareup.moshi.Json

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Trip(
    val summary: Summary = Summary(),
    @field:Json(name = "status_message") val statusMessage: String = "",
    val legs: List<Leg> = listOf(),
    val language: String = "",
    val locations: List<Location> = listOf(),
    val units: String = "",
    val status: Int = 0
)
