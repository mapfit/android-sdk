package com.mapfit.android.annotations

import com.mapfit.android.MapController

/**
 * Defines options for [Marker].
 *
 * Created by dogangulcan on 1/3/18.
 */
class MarkerOptions internal constructor(
    private var marker: Marker,
    internal val mapController: MutableList<MapController>
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

    var width = 55
        set(value) {
            if (!marker.usingDefaultIcon) {
                field = value
                updateStyle()
            }
        }

    var drawOrder = 2000
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

    internal fun setDefaultMarkerSize() {
        marker.usingDefaultIcon = false
        height = 59
        width = 55
        marker.usingDefaultIcon = true
    }

    private fun getStyleString() =
        "{ style: 'sdk-point-overlay', anchor: top, size: [${width}px, ${height}px], order: $drawOrder, interactive: true, collide: false }"

    internal fun updateStyle() {
        marker.mapBindings.forEach {
            it.key.setMarkerStylingFromString(it.value, getStyleString())
            it.key.setMarkerDrawOrder(it.value, drawOrder)
        }
//        marker.mapBindings.forEach {
//        }
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