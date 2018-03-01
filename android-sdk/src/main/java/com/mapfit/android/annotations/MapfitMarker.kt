package com.mapfit.android.annotations

/**
 * Default Mapfit marker icons.
 *
 * Created by dogangulcan on 12/27/17.
 */
enum class MapfitMarker(private val iconUrl: String) {

    DEFAULT("default"),
    ACTIVE("active"),
    AIRPORT("airport"),
    ARTS("arts"),
    AUTO("auto"),
    FINANCE("finance"),
    COMMERCIAL("commercial"),
    CAFE("cafe"),
    CONFERENCE("conference"),
    SPORTS("sports"),
    EDUCATION("education"),
    MARKET("market"),
    COOKING("cooking"),
    GAS("gas"),
    HOMEGARDEN("homegarden"),
    HOSPITAL("hospital"),
    HOTEL("hotel"),
    LAW("law"),
    MEDICAL("medical"),
    BAR("bar"),
    PARK("park"),
    PHARMACY("pharmacy"),
    COMMUNITY("community"),
    RELIGION("religion"),
    RESTAURANT("restaurant"),
    SHOPPING("shopping");

    internal fun getUrl() =
        "http://cdn.mapfit.com/m1/assets/images/markers/pngs/lighttheme/$iconUrl.png"

}
