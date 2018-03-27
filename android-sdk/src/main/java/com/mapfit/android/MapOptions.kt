package com.mapfit.android

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Location
import android.support.annotation.FloatRange
import android.support.annotation.RequiresPermission
import android.support.v4.content.ContextCompat
import android.view.View
import com.mapfit.android.MapView.Companion.ANIMATION_DURATION
import com.mapfit.android.annotations.Anchor
import com.mapfit.android.compass.CompassListener
import com.mapfit.android.compass.CompassProvider
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.isEmpty
import com.mapfit.android.location.*
import com.mapfit.android.utils.getBitmapFromVectorDrawable
import com.mapfit.android.utils.isValidZoomLevel
import com.mapfit.android.utils.rotate
import kotlinx.android.synthetic.main.mf_overlay_map_controls.view.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlin.math.abs


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
    private val compassProvider: CompassProvider by lazy {
        CompassProvider(
            mapView.context,
            compassListener
        )
    }
    private var orientationIconBitmap: Bitmap? = null
    private var listener: LocationListener? = null
    private var previousAccuracyMarkerSize = 0
    private var rotationJob = Job()
    private var orientationMarkerSize = 0
    private var maxZoom: Float = 20f
    private var minZoom: Float = 1f
    private val screenDensity = mapView.context.resources.displayMetrics.densityDpi

    /**
     * Marker for user location.
     */
    private val userMarker by lazy {
        mapController.addMarker().apply {
            setIcon(R.drawable.mf_user_loc)
            markerOptions.apply {
                anchor = Anchor.CENTER
                drawOrder = 1900
                flat = true
            }
        }
    }

    /**
     * Marker for user location accuracy.
     */
    private val accuracyMarker by lazy {
        mapController.addMarker().apply {
            setIcon(R.drawable.mf_accuracy_radius)
            markerOptions.anchor = Anchor.CENTER
            markerOptions.drawOrder = 1000
            markerOptions.flat = true
        }
    }

    /**
     * Marker for user location accuracy.
     */
    private val orientationMarker by lazy {
        mapController.addMarker().apply {

            orientationIconBitmap = getBitmapFromVectorDrawable(
                mapView.context,
                R.drawable.mf_user_direction
            )

            orientationIconBitmap?.let {
                setBitmap(it, mapController)
            }

            markerOptions.apply {
                setSideSize(10, 10)
                anchor = Anchor.CENTER
                drawOrder = 1100
                flat = true
            }

            orientationMarkerSize = orientationIconBitmap?.getScaledWidth(screenDensity) ?: 0
        }
    }

    companion object {
        const val MAP_MIN_ZOOM = 1.0
        const val MAP_MAX_ZOOM = 20.0
    }

    private var userLocationEnabled: Boolean = false

    internal fun getLastLocation() =
        mapfitLocationProvider.lastLocation?.let {
            LatLng(it.latitude, it.longitude)
        }

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

    /**
     * Toggle for user's current location button. The map will be centered on button click.
     */
    var userLocationButtonEnabled = true
        set(value) {
            mapView.btnUserLocation.setImageResource(
                if (!userLocationEnabled || !mapfitLocationProvider.isLocationPermissionGranted()) {
                    R.drawable.mf_current_location_passive
                } else {
                    (R.drawable.mf_current_location)
                }
            )

            mapView.btnUserLocation.isEnabled = userLocationEnabled
            mapView.btnUserLocation.visibility = if (value) View.VISIBLE else View.GONE

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

    /**
     * Enables user location updates and put marker on the map. You MUST have
     * [Manifest.permission.ACCESS_FINE_LOCATION] before enabling. User's location marker will be
     * updated on each location update.
     *
     * @param enable true or false
     * @param locationPriority accuracy for the location updates
     * @param listener location update listener
     */
    @SuppressLint("MissingPermission")
    @JvmOverloads
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun setUserLocationEnabled(
        enable: Boolean,
        locationPriority: LocationPriority = LocationPriority.HIGH_ACCURACY,
        listener: LocationListener? = null
    ) {
        this.listener = listener
        userLocationEnabled = enable

        if (enable && userLocationButtonEnabled) {
            mapView.btnUserLocation.setImageResource(R.drawable.mf_current_location)
            mapView.btnUserLocation.isEnabled = true
        }

        if (enable) {
            compassProvider.start()

            val locationRequest = LocationRequest(locationPriority)
            mapfitLocationProvider.requestLocationUpdates(
                locationRequest,
                locationListener = locationListener
            )

            if (userLocationButtonEnabled) {
                mapView.btnUserLocation.setImageResource(R.drawable.mf_current_location)
                mapView.btnUserLocation.isEnabled = true
            }

        } else {
            compassProvider.stop()
            mapfitLocationProvider.removeLocationUpdates(locationListener)

            mapView.btnUserLocation.setImageResource(R.drawable.mf_current_location_passive)
            mapView.btnUserLocation.isEnabled = false
        }

        userMarker.visibility = enable
        orientationMarker.visibility = enable
        accuracyMarker.visibility = enable
    }

    /**
     * Returns user location status.
     *
     * @return true if user location is enabled
     */
    fun getUserLocationEnabled() = userLocationEnabled

    private val locationListener = object : LocationListener {
        override fun onLocation(location: Location) {
            launch {
                val userLocation = LatLng(location.latitude, location.longitude)

                when (location.accuracy) {
                    in 0..10 -> accuracyMarker.visibility = false
                    else -> {
                        accuracyMarker.visibility = true
                    }
                }

                if (!userLocation.isEmpty()) {
                    userMarker.setPositionEased(userLocation, ANIMATION_DURATION)
                    accuracyMarker.setPositionEased(userLocation, ANIMATION_DURATION)
                    orientationMarker.setPositionEased(userLocation, ANIMATION_DURATION)
                }
            }
            listener?.onLocation(location)
        }

        override fun onProviderStatus(availability: ProviderStatus) {
            listener?.onProviderStatus(availability)
        }
    }

    private var previousAngle = 0f
    private var difference = 0f
    private val compassListener = object : CompassListener {
        override fun onOrientationChanged(angle: Float) {
            difference = abs(previousAngle - angle)

            /* if the difference between previous and current angle is significant. */
            if (difference > 0.8) {
                rotateUserDirection(angle)
                previousAngle = angle
            }
        }
    }

    internal fun rotateUserDirection(angle: Float = -1f) = launch {
        rotationJob = launch {
            orientationIconBitmap?.let {
                val rotateAngle = if (angle == -1f) {
                    compassProvider.azimuth
                } else {
                    angle
                }
                val rotatedBitmap = it.rotate(rotateAngle)
                setOrientationBitmap(rotatedBitmap)
            }
        }
    }

    /**
     * Resize the accuracy circle.
     */
    internal fun resizeAccuracyCircle() = launch {
        if (accuracyMarker.visibility) {
            accuracyMarker.markerOptions.apply {
                val sizeLength = async {
                    mapfitLocationProvider.lastLocation?.latitude?.let {

                        val pixelPerMeter = getPixelsPerMeter(
                            mapView.context,
                            it,
                            this@MapOptions.mapController.zoom
                        )

                        ((mapfitLocationProvider.lastLocation!!.accuracy / 2) * pixelPerMeter).toInt()
                    }
                }

                sizeLength.await()
                    ?.takeIf { abs(previousAccuracyMarkerSize - it) > 20 && it > 15 }
                    ?.let {
                        setSideSize(it, it)
                        previousAccuracyMarkerSize = it
                    }
            }
        }
    }

    private fun setOrientationBitmap(
        bitmap: Bitmap
    ) {
        val argb = IntArray(orientationMarkerSize * orientationMarkerSize)
        bitmap.getPixels(
            argb,
            0,
            orientationMarkerSize,
            0,
            0,
            orientationMarkerSize,
            orientationMarkerSize
        )

        val abgr = IntArray(orientationMarkerSize * orientationMarkerSize)
        var row: Int
        var col: Int
        for (i in argb.indices) {
            col = i % orientationMarkerSize
            row = i / orientationMarkerSize
            val pix = argb[i]
            val pb = pix shr 16 and 0xff
            val pr = pix shl 16 and 0x00ff0000
            val pix1 = pix and -0xff0100 or pr or pb
            val flippedIndex = (orientationMarkerSize - 1 - row) * orientationMarkerSize + col
            abgr[flippedIndex] = pix1
        }

        mapController.setMarkerBitmap(
            orientationMarker.getIdForMap(mapController) ?: 0,
            orientationMarkerSize,
            orientationMarkerSize,
            abgr
        )
    }

    private enum class CameraType {
        PERSPECTIVE,
        ISOMETRIC,
        FLAT
    }

}

