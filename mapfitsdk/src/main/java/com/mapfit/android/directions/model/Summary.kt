package com.mapfit.android.directions.model

import com.squareup.moshi.Json

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Summary(
    @field:Json(name = "min_lon") val minLon: Double,
    @field:Json(name = "max_lat") val maxLat: Double,
    @field:Json(name = "max_lon") val maxLon: Double,
    val length: Double,
    val time: Int,
    @field:Json(name = "min_lat") val minLat: Double
)