package com.mapfit.android.annotations

/**
 *  Represents the shape types of the ends of features
 */
enum class CapType(private val value: String) {
    MITER("0"),
    SQUARE("2"),
    ROUND("5");

    internal fun getValue() = value
}