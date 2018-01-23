package com.mapfit.mapfitsdk.geocoder.model

/**
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

