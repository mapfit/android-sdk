package com.mapfit.android

import android.support.annotation.FloatRange
import android.support.v4.content.ContextCompat
import android.view.View
import com.mapfit.android.utils.isValidZoomLevel
import kotlinx.android.synthetic.main.overlay_map_controls.view.*

/**
 * Map settings and options are manipulated trough this class.
 *
 * Created by dogangulcan on 12/21/17.
 */
class MapOptions internal constructor(
    private val mapView: com.mapfit.android.MapView,
    private val mapController: MapController
) {

    companion object {
        const val MAP_MIN_ZOOM = 1.0
        const val MAP_MAX_ZOOM = 18.0
    }

    private var maxZoom: Float = 18f

    private var minZoom: Float = 1f

    var theme: MapTheme? = null
        set(value) {
            if (field == null || field != value) {
                value?.let { updateScene(value) }
                field = value
            }
        }

    private fun updateScene(value: MapTheme) {
        mapController.loadSceneFile(value.toString())
        updateAttributionImage(value)
    }

    private fun updateAttributionImage(value: MapTheme) {
        val attributionImage = when (value) {
            MapTheme.MAPFIT_GRAYSCALE,
            MapTheme.MAPFIT_DAY -> {
                mapView.btnLegal?.setTextColor(
                    ContextCompat.getColor(
                        mapView.context,
                        R.color.dark_text
                    )
                )
                mapView.btnBuildYourMap?.setTextColor(
                    ContextCompat.getColor(
                        mapView.context,
                        R.color.dark_text
                    )
                )
                R.drawable.ic_watermark_light
            }

            MapTheme.MAPFIT_NIGHT -> {
                mapView.btnLegal?.setTextColor(
                    ContextCompat.getColor(
                        mapView.context,
                        R.color.light_text
                    )
                )
                mapView.btnBuildYourMap?.setTextColor(
                    ContextCompat.getColor(
                        mapView.context,
                        R.color.light_text
                    )
                )
                R.drawable.ic_watermark_dark
            }
        }

        mapView.getAttributionImage().setImageResource(attributionImage)
    }

    var compassButtonEnabled = false
        set(value) {
            mapView.btnCompass.visibility = if (value) View.VISIBLE else View.GONE
            field = value
        }

    var recenterButtonEnabled = false
        set(value) {
            mapView.btnRecenter.visibility = if (value) View.VISIBLE else View.GONE
            field = value
        }

    var zoomControlsEnabled = true
        set(value) {
            mapView.zoomControlsView.visibility = if (value) View.VISIBLE else View.GONE
            field = value
        }

    private var cameraType: CameraType = CameraType.PERSPECTIVE
        set(value) {
            mapController.cameraType = MapController.CameraType.valueOf(value.name)
            field = value
        }

    var panEnabled = true
        set(value) {
            mapController.touchInput.panEnabled = value
            field = value
        }

    var pinchEnabled = true
        set(value) {
            mapController.touchInput.pinchEnabled = value
            field = value
        }

    var rotateEnabled = true
        set(value) {
            mapController.touchInput.rotationEnabled = value
            field = value
        }

    var tiltEnabled = true
        set(value) {
            mapController.touchInput.tiltEnabled = value
            field = value
        }

    /**
     * @param zoomLevel desired maximum zoom level
     */
    fun setMaxZoom(@FloatRange(from = MAP_MIN_ZOOM, to = MAP_MAX_ZOOM) zoomLevel: Float) {
        if (isValidZoomLevel(zoomLevel)) {
            maxZoom = zoomLevel
        }
    }

    /**
     * @param zoomLevel desired minimum zoom level
     */
    fun setMinZoom(@FloatRange(from = MAP_MIN_ZOOM, to = MAP_MAX_ZOOM) zoomLevel: Float) {
        if (isValidZoomLevel(zoomLevel)) {
            minZoom = zoomLevel
        }
    }

    internal fun getMaxZoom() = maxZoom

    internal fun getMinZoom() = minZoom

    private enum class CameraType {
        PERSPECTIVE,
        ISOMETRIC,
        FLAT
    }

}