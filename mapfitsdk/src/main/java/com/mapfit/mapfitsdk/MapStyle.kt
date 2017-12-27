package com.mapfit.mapfitsdk

/**
 * Built in scenes.
 *
 * Created by dogangulcan on 12/21/17.
 */
enum class MapStyle(private val scenePath: String) {
    MAPFIT_DAY("asset:///mapfit-day.yaml"),
    MAPFIT_NIGHT("asset:///mapfit-night.yaml");

    override fun toString(): String {
        return scenePath
    }
}