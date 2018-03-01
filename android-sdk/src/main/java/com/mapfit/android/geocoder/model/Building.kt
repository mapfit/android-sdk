package com.mapfit.android.geocoder.model

import com.mapfit.android.geometry.LatLng

/**
 * Class representing the Building polygon and the polygon type.
 *
 * Created by dogangulcan on 2/22/18.
 */
data class Building(
    val polygon: List<List<LatLng>> = emptyList(),
    val type: String = ""
)