package com.mapfit.mapfitsdk.annotations

import com.mapfit.mapfitsdk.MapController

/**
 * Defines marker options for [Marker]
 *
 * Created by dogangulcan on 1/3/18.
 */
class MarkerOptions internal constructor(
    private var marker: Marker,
    private val mapController: MapController
) {

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

    internal var style =
        "{ style: 'points', anchor: top, color: $color, size: [${height}px, ${width}px], order: $drawOrder, interactive: true, collide: false }"
        set(value) {
            field = value
            updateStyle()
        }

    init {
        updateStyle()
    }

    private fun updateStyle() {
        mapController.setMarkerStylingFromString(marker.getId(), style)
    }

}