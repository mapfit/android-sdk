package com.mapfit.android.annotations

import com.mapfit.android.MapController

/**
 * Defines options for [Marker].
 *
 * Created by dogangulcan on 1/3/18.
 */
class MarkerOptions internal constructor(
    private var marker: Marker
) {

    /**
     * Height of the marker icon in pixels.
     */
    var height = 59
        set(value) {
            if (!marker.usingDefaultIcon) {
                field = value
                updateStyle()
            }
        }

    /**
     * Width of the marker icon in pixels.
     */
    var width = 55
        set(value) {
            if (!marker.usingDefaultIcon) {
                field = value
                updateStyle()
            }
        }

    var flat = false
        set(value) {
            field = value
            updateStyle()
        }

    var drawOrder = 2000
        set(value) {
            field = value
            updateStyle()
        }

    var anchor = Anchor.TOP
        set(value) {
            field = value
            updateStyle()
        }

    private val markerDotSide by lazy {
        11
    }

    private val placeInfoMarkerStyle =
        "{ style: 'sdk-point-overlay', anchor: top, size: [${markerDotSide}px, ${markerDotSide}px], order: $drawOrder, interactive: true, collide: false }"

    init {
        updateStyle()
    }

    @Synchronized
    internal fun setDefaultMarkerSize() {
        marker.usingDefaultIcon = false
        height = 59
        width = 55
        marker.usingDefaultIcon = true
    }

    private fun getStyleString() =
        "{ style: 'sdk-point-overlay', anchor: ${anchor.getAnchor()}, flat: $flat, size: [${width}px, ${height}px], order: $drawOrder, interactive: true, collide: false }"

    @Synchronized
    internal fun updateStyle() {
        val styleString = getStyleString()

        marker.mapBindings.forEach {
            it.key.setMarkerStylingFromString(it.value, styleString)
            it.key.setMarkerDrawOrder(it.value, drawOrder)
        }
    }

    @Synchronized
    internal fun setAccuracyMarkerStyle(
        w: Int,
        h: Int
    ) {
        marker.mapBindings.forEach {
            it.key.setMarkerStylingFromString(
                it.value,
                "{ style: 'sdk-point-overlay', anchor: ${anchor.getAnchor()}, flat: $flat, size: [${w}px, ${h}px], order: $drawOrder, interactive: false, collide: false }"
            )
        }
    }

    internal fun placeInfoShown(
        isShown: Boolean,
        markerId: Long,
        mapController: MapController
    ) {
        if (isShown) {
            mapController.setMarkerStylingFromString(markerId, placeInfoMarkerStyle)

        } else {
            mapController.setMarkerStylingFromString(markerId, getStyleString())
        }
    }

}