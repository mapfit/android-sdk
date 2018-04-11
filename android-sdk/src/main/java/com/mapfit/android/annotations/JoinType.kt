package com.mapfit.android.annotations

/**
 *  Represents the shape types for joints in multi-segment lines
 */
enum class JoinType(private val value: String) {
    MITER("0"),
    BEVEL("1"),
    ROUND("5");

    internal fun getValue() = value
}
