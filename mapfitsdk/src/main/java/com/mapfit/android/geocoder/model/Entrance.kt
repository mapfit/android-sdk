package com.mapfit.android.geocoder.model

/**
 * Data class for defining an entrance point of a place.
 *
 * Created by dogangulcan on 1/18/18.
 */
data class Entrance(
    val latitude: Double,
    val longitude: Double,
    val entranceType: EntranceType?
)