package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.utils.isValidZoomLevel
import com.mapzen.tangram.MapController
import com.mapzen.tangram.MapView

/**
 * Created by dogangulcan on 12/21/17.
 */
class MapOptions internal constructor(
        private val mapView: com.mapfit.mapfitsdk.MapView,
        private val tangramMap: MapController
) {

    var maxZoom: Float = 20.5f
        set(value) {
            if (isValidZoomLevel(value)) {
                field = value
            }
        }

    var minZoom: Float = 20.5f
        set(value) {
            TODO()
        }

    var mapTheme = MapStyle.MAPFIT_NIGHT
        set(value) {
            tangramMap.loadSceneFile(value.toString())
            field = value
        }

    var isCompassVisible = false
        set(value) {
            TODO()
        }

    var isZoomControlsVisible = true
        set(value) {
            mapView.setZoomControlVisibility(value)
            field = value
        }

    var cameraType: CameraType = CameraType.PERSPECTIVE
        set(value) {
            TODO()
        }

    var isPanEnabled = true
        set(value) {
            TODO()
        }

    var isPinchEnabled = true
        set(value) {
            TODO()
        }

    var isRotateEnabled = true
        set(value) {
            TODO()
        }

    var isTiltEnabled = true
        set(value) {
            TODO()
        }

    var is3dBuildingsEnabled = true
        set(value) {
            TODO()
        }


    enum class CameraType {
        PERSPECTIVE,
        ISOMETRIC,
        FLAT
    }
}