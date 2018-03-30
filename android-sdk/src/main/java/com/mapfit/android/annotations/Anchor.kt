package com.mapfit.android.annotations

/**
 * Anchor for Annotations. Since top-left of the screen is (0,0), upper bound of the screen is
 * considered as BOTTOM.
 *
 * Created by dogangulcan on 3/2/18.
 */
enum class Anchor(private val anchor: String) {

    TOP("top"),
    TOP_LEFT("top-left"),
    TOP_RIGHT("top-right"),
    LEFT("left"),
    RIGHT("right"),
    CENTER("center"),
    BOTTOM("bottom"),
    BOTTOM_LEFT("bottom-left"),
    BOTTOM_RIGHT("bottom-right");

    internal fun getAnchor(): String {
        return anchor
    }
}