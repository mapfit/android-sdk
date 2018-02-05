package com.mapfit.mapfitsdk.directions

/**
 * Created by dogangulcan on 2/5/18.
 */
enum class DirectionsType(private val type: String) {
    DRIVING("driving"),
    WALKING("walking"),
    CYCLING("cycling");

    fun getName(): String {
        return type
    }
}