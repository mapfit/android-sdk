package com.mapfit.android.directions.model

import com.squareup.moshi.Json

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Location(
    val lon: Double,
    @field:Json(name = "side_of_street") val sideOfStreet: String,
    val type: String,
    val lat: Double
)