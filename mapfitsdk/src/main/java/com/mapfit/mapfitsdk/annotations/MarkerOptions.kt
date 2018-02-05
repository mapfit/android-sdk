package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.MapController

/**
 * Defines marker options for [Marker]
 *
 * Created by dogangulcan on 1/3/18.
 */
class MarkerOptions internal constructor(
    private var marker: Marker,
    internal val mapController: MutableList<MapController>
) {

    private val markerDotSide by lazy {
        10
    }

    /**
     * Height of the marker in pixels.
     */
    var height = 50
        set(value) {
            if (marker.usingDefaultIcon) {
                field = value
                updateStyle()
            }
        }

    var width = 50
        set(value) {
            if (marker.usingDefaultIcon) {
                field = value
                updateStyle()
            }
        }

    var drawOrder = 2000
        set(value) {
            field = value
            updateStyle()
        }

    var color = "white"
        set(value) {
            field = value
            updateStyle()
        }

    private val placeInfoMarkerStyle =
        "{ style: 'points', anchor: top,color: $color, size: [${markerDotSide}px, ${markerDotSide}px], order: $drawOrder, interactive: true, collide: false }"

    internal var style =
        "{ style: 'points', anchor: top, color: $color, size: [${height}px, ${width}px], order: $drawOrder, interactive: true, collide: false }"
        set(value) {
            field = value
            updateStyle()
        }

    init {
        updateStyle()
    }

    internal fun updateStyle() {
        marker.mapBindings.forEach { it.key.setMarkerStylingFromString(it.value, style) }
    }

    internal fun placeInfoShown(
        isShown: Boolean,
        markerId: Long,
        mapController: MapController
    ) {
        if (isShown) {
            mapController.setMarkerStylingFromString(markerId, placeInfoMarkerStyle)

        } else {
            mapController.setMarkerStylingFromString(markerId, style)

//            updateStyle()
        }
    }

}