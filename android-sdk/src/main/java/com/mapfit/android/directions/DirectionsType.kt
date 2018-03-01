package com.mapfit.android.directions

/**
 * Directions types available for Mapfit Directions API.
 *
 * Created by dogangulcan on 2/5/18.
 */
enum class DirectionsType(private val type: String) {
    DRIVING("driving"),
    WALKING("walking"),
    CYCLING("cycling");

    internal fun getName(): String {
        return type
    }
}