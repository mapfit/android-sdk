package com.mapfit.android.location

/**
 * Bundle for location request preferences.
 *
 * Created by dogangulcan on 3/2/18.
 *
 * @param locationPriority
 * @param interval location will be obtained
 * @param minimumDisplacement in meters to obtain new location
 * @param updateCount number of location updates to be obtained
 */
data class LocationRequest(
    var locationPriority: LocationPriority = LocationPriority.ACCURATE,
    var interval: Long = DEFAULT_LOCATION_UPDATE_INTERVAL,
    var minimumDisplacement: Float = DEFAULT_LOCATION_UPDATE_DISTANCE,
    var updateCount: Int = -1
)

private const val DEFAULT_LOCATION_UPDATE_INTERVAL = 1000L
private const val DEFAULT_LOCATION_UPDATE_DISTANCE = 1f