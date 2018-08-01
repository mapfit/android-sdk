package com.mapfit.android


/**
 * Built in Mapfit map themes.
 */
enum class MapTheme(private val scenePath: String) {
    MAPFIT_DAY("https://cdn.mapfit.com/v3-0/themes/mapfit-day.yaml"),
    MAPFIT_NIGHT("https://cdn.mapfit.com/v3-0/themes/mapfit-night.yaml"),
    MAPFIT_GRAYSCALE("https://cdn.mapfit.com/v3-0/themes/mapfit-grayscale.yaml");

    override fun toString(): String {
        return scenePath
    }
}