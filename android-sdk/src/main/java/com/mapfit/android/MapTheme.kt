package com.mapfit.android


/**
 * Built in Mapfit map themes.
 *
 * Created by dogangulcan on 12/21/17.
 */
enum class MapTheme(private val scenePath: String) {
    MAPFIT_DAY("mapfit-sample_day.yaml"),
    MAPFIT_NIGHT("https://cdn.mapfit.com/v2-2/themes/mapfit-night.yaml"),
    MAPFIT_GRAYSCALE("https://cdn.mapfit.com/v2-2/themes/mapfit-grayscale.yaml");

    override fun toString(): String {
        return scenePath
    }
}
