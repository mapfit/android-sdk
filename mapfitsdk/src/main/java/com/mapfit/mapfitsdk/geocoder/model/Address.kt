package com.mapfit.mapfitsdk.geocoder.model

/**
 * Class represent an address for Mapfit Geocoding API.
 *
 * Created by dogangulcan on 1/18/18.
 */
data class Address(
        val streetAddress: String = "",
        val country: String = "",
        val adminArea: String = "",
        val locality: String = "",
        val postalCode: String = "",
        val neighborhood: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val status: LocationStatus?,
        val entrances: List<Entrance> = mutableListOf()
)
