package com.mapfit.mapfitsdk

import android.support.annotation.FloatRange
import com.mapfit.mapfitsdk.utils.isValidZoomLevel
import com.mapzen.tangram.MapController

/**
 * Created by dogangulcan on 12/21/17.
 */
class MapOptions internal constructor(
        private val mapView: com.mapfit.mapfitsdk.MapView,
        private val tangramMap: MapController
) {

    companion object {
        const val MAP_MIN_ZOOM = 1.0
        const val MAP_MAX_ZOOM = 20.5
    }

    private var maxZoom: Float = 20.5f

    private var minZoom: Float = 1f

    internal var mapTheme = MapTheme.MAPFIT_DAY
        set(value) {
            tangramMap.loadSceneFile(value.toString())
            field = value
        }

    private var isCompassVisible = false
        set(value) {
            TODO()
        }

    private var isZoomControlsVisible = true
        set(value) {
            mapView.setZoomControlVisibility(value)
            field = value
        }

    private var cameraType: CameraType = CameraType.PERSPECTIVE
        set(value) {
            tangramMap.cameraType = MapController.CameraType.valueOf(value.name)
            field = value
        }

    private var isPanEnabled = true
        set(value) {
            TODO()
        }

    private var isPinchEnabled = true
        set(value) {
            TODO()
        }

    private var isRotateEnabled = true
        set(value) {
            TODO()
        }

    private var isTiltEnabled = true
        set(value) {
            TODO()
        }

    private var is3dBuildingsEnabled = true
        set(value) {
            TODO()
        }


    /**
     * @param zoomLevel desired maximum zoom level
     */
    internal fun setMaxZoom(@FloatRange(from = MAP_MIN_ZOOM, to = MAP_MAX_ZOOM) zoomLevel: Float) {
        if (isValidZoomLevel(zoomLevel)) {
            maxZoom = zoomLevel
        }
    }

    internal fun getMaxZoom() = maxZoom

    internal fun setMinZoom(@FloatRange(from = MAP_MIN_ZOOM, to = MAP_MAX_ZOOM) zoomLevel: Float) {
        if (isValidZoomLevel(zoomLevel)) {
            minZoom = zoomLevel
        }
    }

    private enum class CameraType {
        PERSPECTIVE,
        ISOMETRIC,
        FLAT
    }
}