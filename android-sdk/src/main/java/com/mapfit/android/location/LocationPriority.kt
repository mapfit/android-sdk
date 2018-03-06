package com.mapfit.android.location

/**
 * Location request priorities.
 */
enum class LocationPriority(private val priority: Int) {
    /**
     * Most accurate location will be obtained. It will consume more power. Expect an accuracy of
     * less than 100 meters.
     */
    ACCURATE(3),

    /**
     * Balanced in terms of accuracy and power consumption. Expect an accuracy between 100 and 500
     * meters.
     */
    BALANCED(2),

    /**
     * Focuses on low power consumption. Expect an accuracy greater than 500 meters.
     */
    LOW_POWER(1);

    internal fun getPriority(): Int {
        return priority
    }
}