package com.mapfit.android.geocoder.model

import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.LatLngBounds

/**
 * Class representing the result from the Mapfit Geocoding API, including the normalized address
 * components, geographical coordinate position and the (optional) building polygon.
 *
 * Created by dogangulcan on 1/18/18.
 */
data class Address internal constructor(
    val streetAddress: String = "",
    val country: String = "",
    val adminArea: String = "",
    val locality: String = "",
    val postalCode: String = "",
    val neighborhood: String = "",
    val building: Building,
    val viewport: LatLngBounds?,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val responseType: ResponseType?,
    val entrances: List<Entrance> = mutableListOf()
) {

    internal fun getPrimaryEntrance(): LatLng =
        if (entrances.isNotEmpty()) {
            LatLng(entrances.first().lat, entrances.first().lng)
        } else {
            LatLng(lat, lng)
        }

}
