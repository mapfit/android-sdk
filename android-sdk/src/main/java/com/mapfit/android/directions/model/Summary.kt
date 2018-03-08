package com.mapfit.android.directions.model

import com.squareup.moshi.Json

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Summary(
    @field:Json(name = "min_lon") val minLon: Double = 0.0,
    @field:Json(name = "max_lat") val maxLat: Double = 0.0,
    @field:Json(name = "max_lon") val maxLon: Double = 0.0,
    val length: Double = 0.0,
    val time: Int = 0,
    @field:Json(name = "min_lat") val minLat: Double = 0.0
)