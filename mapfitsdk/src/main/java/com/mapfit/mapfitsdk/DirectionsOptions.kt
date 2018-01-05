package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.geo.LatLng

/**
 * Created by dogangulcan on 1/4/18.
 */
class DirectionsOptions {

    var isVisible = false
        set(value) {
            TODO()
        }

    private var origin: LatLng? = null

    private var destination: LatLng? = null

    private var type: DirectionsType = DirectionsType.DRIVE

    fun setOrigin(): DirectionsOptions {
        TODO()
        return this
    }

    fun setDestination(): DirectionsOptions {
        TODO()
        return this
    }

    fun setType(): DirectionsOptions {
        TODO()
        return this
    }

    fun showDirections() {
        TODO()
    }

    enum class DirectionsType {
        DRIVE,
        WALK,
        BIKE
    }

}