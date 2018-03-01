package com.mapfit.android


/**
 * Built in Mapfit map themes.
 *
 * Created by dogangulcan on 12/21/17.
 */
enum class MapTheme(private val scenePath: String) {
    MAPFIT_DAY("asset:///mapfit-day.yaml"),
    MAPFIT_NIGHT("asset:///mapfit-night.yaml"),
    MAPFIT_GRAYSCALE("asset:///mapfit-grayscale.yaml");

    override fun toString(): String {
        return scenePath
    }
}
