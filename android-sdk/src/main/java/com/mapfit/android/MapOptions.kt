package com.mapfit.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.location.Location
import android.support.annotation.FloatRange
import android.support.annotation.RequiresPermission
import android.support.v4.content.ContextCompat
import android.view.View
import com.mapfit.android.MapView.Companion.ANIMATION_DURATION
import com.mapfit.android.annotations.Anchor
import com.mapfit.android.annotations.MarkerOptions
import com.mapfit.android.compass.CompassListener
import com.mapfit.android.compass.CompassProvider
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.isEmpty
import com.mapfit.android.location.*
import com.mapfit.android.utils.getBitmapFromVectorDrawable
import com.mapfit.android.utils.isValidZoomLevel
import com.mapfit.android.utils.rotate
import com.mapfit.tetragon.SceneUpdate
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
    private var userLocationEnabled: Boolean = false
    private var previousAngle = 0f
    private var difference = 0f
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

                userMarker.visibility = !userLocation.isEmpty()
                accuracyMarker.visibility = !userLocation.isEmpty()
                orientationMarker.visibility = !userLocation.isEmpty()
            }

            listener?.onLocation(location)
        }

        override fun onProviderStatus(status: ProviderStatus) {
            listener?.onProviderStatus(status)
        }
    }
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

    /**
     * Marker for user location.
     */
    private val userMarker by lazy {
        val markerOptions = MarkerOptions()
            .icon(R.drawable.mf_user_loc)
            .anchor(Anchor.CENTER)
            .flat(true)
            .drawOrder(2110)

        mapController.addMarker(markerOptions)
    }

    /**
     * Marker for user location accuracy.
     */
    private val accuracyMarker by lazy {
        val markerOptions = MarkerOptions()
            .icon(R.drawable.mf_accuracy_circle)
            .anchor(Anchor.CENTER)
            .flat(true)
            .drawOrder(2100)

        mapController.addMarker(markerOptions)
    }

    /**
     * Marker for user location accuracy.
     */
    private val orientationMarker by lazy {
        val markerOptions = MarkerOptions()
            .anchor(Anchor.CENTER)
            .flat(true)
            .drawOrder(2105)

        orientationIconBitmap = getBitmapFromVectorDrawable(
            mapView.context,
            R.drawable.mf_user_direction
        )

        orientationIconBitmap?.let {
            markerOptions.icon(it)
            orientationMarkerSize = orientationIconBitmap?.getScaledWidth(screenDensity) ?: 0
        }

        mapController.addMarker(markerOptions)
    }

    internal fun getLastLocation() =
        mapfitLocationProvider.lastLocation?.let { LatLng(it.latitude, it.longitude) }

    var theme: MapTheme? = null
        set(value) {
            if (field == null || field != value) {
                value?.let { loadScene(value) }
            }
            field = value
        }

    var customTheme: String? = null
        set(value) {
            mapController.loadSceneFileAsync(value ?: "")
            theme = null
            field = value
        }

    var cameraType: CameraType = CameraType.PERSPECTIVE
        set(value) {
            mapController.cameraType = MapController.CameraType.valueOf(value.name)
            field = value
        }

    var isCompassButtonVisible = false
        set(value) {
            mapView.btnCompass.visibility = if (value) View.VISIBLE else View.GONE
            field = value
        }

    var isRecenterButtonVisible = false
        set(value) {
            mapView.btnRecenter.visibility = if (value) View.VISIBLE else View.GONE
            field = value
        }

    var isUserLocationButtonVisible = true
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

    var isZoomControlVisible = true
        set(value) {
            mapView.zoomControlsView.visibility = if (value) View.VISIBLE else View.GONE
            field = value
        }

    var gesturesEnabled = true
        set(value) {
            isPanEnabled = value
            isPinchEnabled = value
            isRotateEnabled = value
            isTiltEnabled = value
            field = value
        }

    var isPanEnabled = true
        set(value) {
            mapController.touchInput.panEnabled = value
            field = value
        }

    var isPinchEnabled = true
        set(value) {
            mapController.touchInput.pinchEnabled = value
            field = value
        }

    var isRotateEnabled = true
        set(value) {
            mapController.touchInput.rotationEnabled = value
            field = value
        }

    var isTiltEnabled = true
        set(value) {
            mapController.touchInput.tiltEnabled = value
            field = value
        }

    var is3dBuildingsEnabled = false
        set(value) {
            mapController.enable3dBuildings(value)
            field = value
        }

    var isTransitLayerEnabled = false
        set(value) {
            mapController.enableTransitLayer(value)
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

    /**
     * Applies SceneUpdates to the current scene asynchronously. When the updates are finished,
     * [OnMapThemeLoadListener] will be triggered if set.
     *
     * @param sceneUpdates list of the updates
     */
    fun updateScene(sceneUpdates: List<SceneUpdate>) {
        mapController.updateSceneAsync(sceneUpdates)
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

        if (enable && isUserLocationButtonVisible) {
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

            if (isUserLocationButtonVisible) {
                mapView.btnUserLocation.setImageResource(R.drawable.mf_current_location)
                mapView.btnUserLocation.isEnabled = true
            }

        } else {
            compassProvider.stop()
            mapfitLocationProvider.removeLocationUpdates(locationListener)

            mapView.btnUserLocation.setImageResource(R.drawable.mf_current_location_passive)
            mapView.btnUserLocation.isEnabled = false
        }

        // hide before a location update
        userMarker.visibility = false
        accuracyMarker.visibility = false
        orientationMarker.visibility = false
    }

    /**
     * Returns user location status.
     *
     * @return true if user location is enabled
     */
    fun getUserLocationEnabled() = userLocationEnabled

    internal fun getMaxZoom() = maxZoom

    internal fun getMinZoom() = minZoom

    /**
     * Rotates and sets users' orientation marker accordingly to the given angle.
     *
     * @param angle to be rotated to
     */
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
            accuracyMarker.apply {
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
                    ?.takeIf { it > 30 }
                    ?.let {
                        setAccuracyMarkerStyle(it, it)
                        previousAccuracyMarkerSize = it
                    }
            }
        }
    }

    /**
     *  Returns the offset to fix misplacement caused by vanishing point for [MapTheme]s.
     *
     *  @return default X and Y axis offset in pixels
     */
    internal fun getVanishingPointOffset(): Pair<Float, Float> {
        val offsetX = if (customTheme.isNullOrEmpty()) {
            VANISHING_POINT_OFFSET_X
        } else {
            0f
        }.div(Resources.getSystem()?.displayMetrics?.density ?: 1f)

        val offsetY = if (customTheme.isNullOrEmpty()) {
            VANISHING_POINT_OFFSET_Y
        } else {
            0f
        }.div(Resources.getSystem()?.displayMetrics?.density ?: 1f)

        return Pair(offsetX, offsetY)
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

        mapView.attributionImage.setImageResource(attributionImage)
    }

    private fun loadScene(value: MapTheme) {
        mapController.loadSceneFileAsync(value.toString())
        updateAttributionImage(value)
    }

    companion object {
        const val MAP_MIN_ZOOM = 1.0
        const val MAP_MAX_ZOOM = 20.0
        const val VANISHING_POINT_OFFSET_Y = 125f
        const val VANISHING_POINT_OFFSET_X = 0f
    }

    enum class CameraType {
        PERSPECTIVE,
        ISOMETRIC,
        FLAT
    }

}