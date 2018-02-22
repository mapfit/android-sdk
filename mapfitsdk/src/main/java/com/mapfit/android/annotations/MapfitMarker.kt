package com.mapfit.android.annotations

/**
 * Default Mapfit marker icons.
 *
 * Created by dogangulcan on 12/27/17.
 */
enum class MapfitMarker(private val iconUrl: String) {

    DEFAULT("lighttheme/default"),
    ACTIVE("lighttheme/active"),
    AIRPORT("lighttheme/airport"),
    ARTS("lighttheme/arts"),
    AUTO("lighttheme/auto"),
    FINANCE("lighttheme/finance"),
    COMMERCIAL("lighttheme/commercial"),
    CAFE("lighttheme/cafe"),
    CONFERENCE("lighttheme/conference"),
    SPORTS("lighttheme/sports"),
    EDUCATION("lighttheme/education"),
    MARKET("lighttheme/market"),
    COOKING("lighttheme/cooking"),
    GAS("lighttheme/gas"),
    HOMEGARDEN("lighttheme/homegarden"),
    HOSPITAL("lighttheme/hospital"),
    HOTEL("lighttheme/hotel"),
    LAW("lighttheme/law"),
    MEDICAL("lighttheme/medical"),
    BAR("lighttheme/bar"),
    PARK("lighttheme/park"),
    PHARMACY("lighttheme/pharmacy"),
    COMMUNITY("lighttheme/community"),
    RELIGION("lighttheme/religion"),
    RESTAURANT("lighttheme/restaurant"),
    SHOPPING("lighttheme/shopping");

    internal fun getUrl() = "http://cdn.stg.mapfit.com/v2/assets/images/markers/pngs/$iconUrl.png"

}
