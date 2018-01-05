package com.mapfit.mapfitsdk.annotations

/**
 * Created by dogangulcan on 12/27/17.
 */
enum class MapfitMarker(private val iconUrl: String) {

    DARK_ACTIVE("darktheme/active"),
    DARK_AIRPORT("darktheme/airport"),
    DARK_ARTS("darktheme/arts"),
    DARK_AUTO("darktheme/auto"),
    DARK_FINANCE("darktheme/finance"),
    DARK_COMMERCIAL("darktheme/commercial"),
    DARK_CAFE("darktheme/cafe"),
    DARK_CONFERENCE("darktheme/conference"),
    DARK_SPORTS("darktheme/sports"),
    DARK_EDUCATION("darktheme/education"),
    DARK_MARKET("darktheme/market"),
    DARK_COOKING("darktheme/cooking"),
    DARK_GAS("darktheme/gas"),
    DARK_HOMEGARDEN("darktheme/homegarden"),
    DARK_HOSPITAL("darktheme/hospital"),
    DARK_HOTEL("darktheme/hotel"),
    DARK_LAW("darktheme/law"),
    DARK_MEDICAL("darktheme/medical"),
    DARK_BAR("darktheme/bar"),
    DARK_PARK("darktheme/park"),
    DARK_PHARMACY("darktheme/pharmacy"),
    DARK_COMMUNITY("darktheme/community"),
    DARK_RELIGION("darktheme/religion"),
    DARK_RESTAURANT("darktheme/restaurant"),
    DARK_SHOPPING("darktheme/shopping"),

    LIGHT_ACTIVE("lighttheme/active"),
    LIGHT_AIRPORT("lighttheme/airport"),
    LIGHT_ARTS("lighttheme/arts"),
    LIGHT_AUTO("lighttheme/auto"),
    LIGHT_FINANCE("lighttheme/finance"),
    LIGHT_COMMERCIAL("lighttheme/commercial"),
    LIGHT_CAFE("lighttheme/cafe"),
    LIGHT_CONFERENCE("lighttheme/conference"),
    LIGHT_SPORTS("lighttheme/sports"),
    LIGHT_EDUCATION("lighttheme/education"),
    LIGHT_MARKET("lighttheme/market"),
    LIGHT_COOKING("lighttheme/cooking"),
    LIGHT_GAS("lighttheme/gas"),
    LIGHT_HOMEGARDEN("lighttheme/homegarden"),
    LIGHT_HOSPITAL("lighttheme/hospital"),
    LIGHT_HOTEL("lighttheme/hotel"),
    LIGHT_LAW("lighttheme/law"),
    LIGHT_MEDICAL("lighttheme/medical"),
    LIGHT_BAR("lighttheme/bar"),
    LIGHT_PARK("lighttheme/park"),
    LIGHT_PHARMACY("lighttheme/pharmacy"),
    LIGHT_COMMUNITY("lighttheme/community"),
    LIGHT_RELIGION("lighttheme/religion"),
    LIGHT_RESTAURANT("lighttheme/restaurant"),
    LIGHT_SHOPPING("lighttheme/shopping");

    fun getMarkerUrl() = "https://cdn.mapfit.com/v1/assets/images/markers/pngs/$iconUrl.png"

}
