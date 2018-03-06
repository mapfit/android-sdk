package com.mapfit.android

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.support.annotation.FloatRange
import android.support.annotation.RequiresPermission
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import com.mapfit.android.annotations.Anchor
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.location.LocationListener
import com.mapfit.android.location.MapfitLocationProvider
import com.mapfit.android.location.ProviderStatus
import com.mapfit.android.location.getPixelsPerMeter
import com.mapfit.android.utils.isValidZoomLevel
import kotlinx.android.synthetic.main.mf_overlay_map_controls.view.*
import java.util.*

/**
 * Map settings and options are manipulated trough this class.
 *
 * Created by dogangulcan on 12/21/17.
 */
class MapOptions internal constructor(
    private val mapView: com.mapfit.android.MapView,
    private val mapController: MapController
) {

    private val mapfitLocationProvider by lazy { MapfitLocationProvider(mapView.context) }

    /**
     * Marker for user location.
     */
    private val userMarker by lazy {
        mapController.addMarker().apply {
            setIcon(R.drawable.mf_user_location)
            markerOptions.width = 50
            markerOptions.height = 38
            markerOptions.anchor = Anchor.CENTER
            markerOptions.drawOrder = 3000
        }
    }

    /**
     * Marker for user location accuracy.
     */
    private val accuracyMarker by lazy {
        mapController.addMarker().apply {
            setIcon(R.drawable.mf_accuracy_radius)
            markerOptions.width = 100
            markerOptions.height = 100
            markerOptions.anchor = Anchor.CENTER
            markerOptions.drawOrder = 1000
        }
    }

    companion object {
        const val MAP_MIN_ZOOM = 1.0
        const val MAP_MAX_ZOOM = 20.0
    }

    private var maxZoom: Float = 20f

    private var minZoom: Float = 1f

    var theme: MapTheme? = null
        set(value) {
            if (field == null || field != value) {
                value?.let { updateScene(value) }
                field = value
            }
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
                R.drawable.mf_watermark_light
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
                R.drawable.mf_watermark_dark
            }
        }

        mapView.getAttributionImage().setImageResource(attributionImage)
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun setUserLocationEnabled(enable: Boolean) {
        if (enable) {
            mapfitLocationProvider.requestLocationUpdates(locationListener = locationListener)
        } else {
            mapfitLocationProvider.removeLocationUpdates(locationListener)
        }

        mapController.zoom = 16f
    }

    private val locationListener = object : LocationListener {
        override fun onLocation(location: Location) {
            val userLocation = LatLng(location.latitude, location.longitude)
            userMarker.setPositionEased(userLocation, 200)
            accuracyMarker.setPositionEased(userLocation, 200)

            mapController.setPositionEased(LatLng(location.latitude, location.longitude), 200)
            location.accuracy = (50..150).random().toFloat()

            when (location.accuracy) {
                in 0..10 -> accuracyMarker.visibility = false
                else -> {
                    accuracyMarker.visibility = true

                    resizeAccuracyMarker()

//                    repeat(100000) {
//                        launch {
//                            delay(200)
//                        }
//                    }

                }
            }
        }

        override fun onProviderStatus(availability: ProviderStatus) {

        }
    }

    fun ClosedRange<Int>.random() =
        Random().nextInt(endInclusive - start) + start

    internal fun resizeAccuracyMarker() {
        accuracyMarker.markerOptions.apply {
            //                        val sideLenght = (100 + location.accuracy).toInt()
            val pixelMeter = mapfitLocationProvider.lastLocation?.latitude?.let {

                getPixelsPerMeter(
                    mapView.context,
                    it,
                    this@MapOptions.mapController.zoom
                )
            }

            pixelMeter?.let {
                val sideLength =
                    ((mapfitLocationProvider.lastLocation!!.accuracy / 2) * it).toInt()
                height = sideLength
                width = sideLength
                Log.d(
                    "SIDESET",
                    "ACCURACY:${mapfitLocationProvider.lastLocation!!.accuracy}\nMETER PIXEL: $it\nSIDE LENGHT: $height"
                )

            }

        }
    }


    private enum class CameraType {
        PERSPECTIVE,
        ISOMETRIC,
        FLAT
    }

}