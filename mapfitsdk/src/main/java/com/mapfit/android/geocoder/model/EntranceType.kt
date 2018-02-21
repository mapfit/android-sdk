package com.mapfit.android.geocoder.model

/**
 * Entrance types of a place.
 *
 * Created by dogangulcan on 1/18/18.
 */
enum class EntranceType(val sourceName: String) {
    PEDESTRIAN_PRIMARY("pedestrian-primary"),
    PEDESTRIAN_SECONDARY("pedestrian-secondary"),
    ALL_PEDESTRIAN("all-pedestrian"),
    LOADING("loading"),
    SERVICE("service"),
    PARKING("parking"),
    ALL("all")
}

