package com.mapfit.android.directions.model

/**
 * Created by dogangulcan on 2/4/18.
 */
data class Summary(
    val min_lon: Double,
    val max_lat: Double,
    val max_lon: Double,
    val length: Double,
    val time: Int,
    val min_lat: Double
)