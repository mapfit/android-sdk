package com.mapfit.android.location

/**
 * Location request priorities.
 */
enum class LocationPriority(private val priority: Int) {
    /**
     * Most accurate location will be obtained. It will consume more power. Expect an accuracy of
     * less than 100 meters.
     */
    HIGH_ACCURACY(3),

    /**
     * Focuses on low power consumption. Expect an accuracy greater than 500 meters.
     */
    LOW_ACCURACY(1);

    internal fun getPriority(): Int {
        return priority
    }
}