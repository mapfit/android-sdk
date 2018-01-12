package com.mapfit.mapfitsdk.annotations

import com.mapzen.tangram.Marker

/**
 * Defines marker options for [Marker]
 *
 * Created by dogangulcan on 1/3/18.
 */
class MarkerOptions(private val tgMarker: Marker?) {

    /**
     * Height of the marker in pixels.
     */
    var height = 50
        set(value) {
            field = value
            updateStype()
        }

    var width = 50
        set(value) {
            field = value
            updateStype()
        }

    var drawOrder = 2000
        set(value) {
            field = value
            updateStype()
        }

    var color = "white"
        set(value) {
            field = value
            updateStype()
        }

    internal var style = "{ style: 'points', anchor: top, color: $color, size: [${height}px, ${width}px], order: $drawOrder, interactive: true, collide: false }"

    private fun updateStype() {
        tgMarker?.setStylingFromString(style)
    }

}