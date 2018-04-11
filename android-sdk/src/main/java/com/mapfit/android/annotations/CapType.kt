package com.mapfit.android.annotations

/**
 *  Enumerates the shape types for the end of line shapes.
 */
enum class CapType(private val value: String) {
    MITER("0"),
    SQUARE("2"),
    ROUND("6");

    internal fun getValue() = value
}