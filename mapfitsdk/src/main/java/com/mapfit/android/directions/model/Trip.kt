package com.mapfit.android.directions.model

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Trip(
    val summary: Summary,
    val status_message: String,
    val legs: List<Leg>,
    val language: String,
    val locations: List<Location>,
    val units: String,
    val status: Int
)
