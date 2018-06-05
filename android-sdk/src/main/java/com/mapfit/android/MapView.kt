package com.mapfit.android

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.net.Uri
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatDelegate
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.mapfit.android.annotations.*
import com.mapfit.android.annotations.Annotation
import com.mapfit.android.annotations.callback.OnMarkerClickListener
import com.mapfit.android.annotations.callback.OnPolygonClickListener
import com.mapfit.android.annotations.callback.OnPolylineClickListener
import com.mapfit.android.annotations.widget.PlaceInfo
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.utils.logWarning
import com.mapfit.android.utils.startActivitySafe
import com.mapfit.tetragon.CachePolicy
import com.mapfit.tetragon.ConfigChooser
import com.mapfit.tetragon.HttpHandler
import com.mapfit.tetragon.TouchInput
import kotlinx.android.synthetic.main.mf_overlay_map_controls.view.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import okhttp3.CacheControl
import okhttp3.HttpUrl
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * The view map is drawn on to.
 *
 * Created by dogangulcan on 12/18/17.
 */
class MapView(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    companion object {
        internal const val ANIMATION_DURATION = 200
        internal val DEFAULT_EASE = MapController.EaseType.CUBIC
        private const val ZOOM_STEP_LEVEL = 1
    }

    private lateinit var mapController: MapController
    internal lateinit var mapOptions: MapOptions
    internal lateinit var directionsOptions: DirectionsOptions

    // Views
    private val controlsView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.mf_overlay_map_controls, this, false)
    }
    private val placeInfoFrame = FrameLayout(context)
    private val attributionImage: ImageView = controlsView.findViewById(R.id.imgAttribution)
    internal val zoomControlsView: RelativeLayout by lazy {
        controlsView.findViewById<RelativeLayout>(R.id.zoomControls)
    }

    @JvmSynthetic
    internal fun getAttributionImage(): ImageView = attributionImage

    internal val layers = mutableListOf<Layer>()

    // event listeners
    internal var markerClickListener: OnMarkerClickListener? = null
    internal var polylineClickListener: OnPolylineClickListener? = null
    internal var polygonClickListener: OnPolygonClickListener? = null
    internal var mapClickListener: OnMapClickListener? = null
    internal var mapDoubleClickListener: OnMapDoubleClickListener? = null
    internal var mapLongClickListener: OnMapLongClickListener? = null
    internal var mapPanListener: OnMapPanListener? = null
    internal var mapThemeLoadListener: OnMapThemeLoadListener? = null
    internal var mapPinchListener: OnMapPinchListener? = null
    internal var placeInfoAdapter: MapfitMap.PlaceInfoAdapter? = null
    internal var onPlaceInfoClickListener: MapfitMap.OnPlaceInfoClickListener? = null

    internal var viewWidth: Int? = null
    internal var viewHeight: Int? = null

    internal var activePlaceInfo: PlaceInfo? = null
    private var placeInfoRemoveJob = Job()
    private var reCentered = false
    private var sceneUpdateFlag = false
    private var animatingCompass = false
    private var compassPivotCenter = 60f

    private lateinit var mapfitMap: MapfitMap

    init {
        Mapfit.getApiKey()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    /**
     * Instantiates the MapView and the controller asynchronously and returns the [MapfitMap]
     * to the given callback. This method must be invoked to display the map.
     *
     * @param mapTheme
     * @param onMapReadyCallback
     */
    @JvmOverloads
    fun getMapAsync(
        mapTheme: MapTheme = MapTheme.MAPFIT_DAY,
        @NotNull onMapReadyCallback: OnMapReadyCallback
    ) {
        initMapAsync(mapTheme = mapTheme, onMapReadyCallback = onMapReadyCallback)
    }

    /**
     * Instantiates the MapView and the controller asynchronously and returns the [MapfitMap]
     * to the given callback. This method must be invoked to display the map.
     *
     * @param customTheme url or file path of the yaml file
     */
    fun getMapAsync(customTheme: String, @NotNull onMapReadyCallback: OnMapReadyCallback) {
        initMapAsync(customTheme = customTheme, onMapReadyCallback = onMapReadyCallback)
    }

    private fun initMapAsync(
        customTheme: String = "",
        mapTheme: MapTheme = MapTheme.MAPFIT_DAY,
        @NotNull onMapReadyCallback: OnMapReadyCallback
    ) {
        if (::mapController.isInitialized) {
            onMapReadyCallback.onMapReady(mapfitMap)
        }

        initMapController(customTheme, mapTheme, onMapReadyCallback)
        initUiControls()
    }

    private fun initMapController(
        customTheme: String = "",
        mapTheme: MapTheme,
        onMapReadyCallback: OnMapReadyCallback
    ) {
        mapController = MapController(getGLSurfaceView())

        mapfitMap = MapfitMap(
            this,
            mapController
        )

        mapController.apply {
            setHttpHandler(getHttpHandler())

            init()

            setTapResponder(singleTapResponder())
            setDoubleTapResponder(doubleTapResponder())
            setLongPressResponder(longClickResponder())
            setPanResponder(panResponder())
            setShoveResponder(shoveResponder())
            setScaleResponder(scaleResponder())
            setRotateResponder(rotateResponder())

            setAnnotationClickListener(onAnnotationClickListener)

            setSceneLoadListener { _, sceneError ->

                if (!sceneUpdateFlag) {
                    onMapReadyCallback.onMapReady(mapfitMap)
                    sceneUpdateFlag = true
                }

                mapThemeLoadListener?.let {
                    if (sceneError == null) {
                        it.onLoaded()
                    } else {
                        it.onError()
                    }
                }
            }

            mapOptions = MapOptions(this@MapView, this)
            directionsOptions = DirectionsOptions(this)

            if (customTheme.isBlank()) {
                mapOptions.theme = mapTheme
            } else {
                mapOptions.customTheme = customTheme
            }

        }
    }

    private fun initUiControls() {
        addView(placeInfoFrame)
        addView(controlsView)

        attributionImage.setOnClickListener {
            attributionAnimJob?.cancel()

            if (containerAttribute.visibility == View.VISIBLE) {
                containerAttribute.visibility = View.GONE
            } else {
                containerAttribute.visibility = View.VISIBLE
            }

            attributionAnimJob = launch {
                delay(2000)
                async(UI) {
                    if (containerAttribute.visibility == View.VISIBLE) {
                        containerAttribute.visibility = View.GONE
                    }
                }
            }
        }

        btnLegal.setOnClickListener {
            context.startActivitySafe(mapfitLegalIntent)
        }

        btnBuildYourMap.setOnClickListener {
            context.startActivitySafe(mapfitWebsiteIntent)
        }

        btnZoomIn.setOnClickListener {
            mapfitMap.setZoom(mapfitMap.getZoom() + ZOOM_STEP_LEVEL, ANIMATION_DURATION.toLong())
        }

        btnZoomOut.setOnClickListener {
            mapfitMap.setZoom(mapfitMap.getZoom() - ZOOM_STEP_LEVEL, ANIMATION_DURATION.toLong())
        }

        btnRecenter.setOnClickListener {
            onReCenterStateChanged(true)
        }

        btnCompass.scaleType = ImageView.ScaleType.MATRIX   // required for rotation

        val xPivot = (btnCompass.drawable.intrinsicWidth / 2).toFloat()
        val yPivot = (btnCompass.drawable.intrinsicHeight / 2).toFloat()

        compassPivotCenter = xPivot

        btnCompass.setOnClickListener {
            launch {
                val currentAngle = (Math.toDegrees(mapController.rotation.toDouble()) + 360) % 360

                val toAngle = if (currentAngle > 180) {
                    360 - currentAngle
                } else {
                    -currentAngle
                }

                val anim = RotateAnimation(
                    0f,
                    toAngle.toFloat(),
                    xPivot,
                    yPivot
                )

                hideCompassButton()

                anim.duration = ANIMATION_DURATION.toLong()
                btnCompass.startAnimation(anim)

                mapController.setRotationEased(0f, ANIMATION_DURATION, DEFAULT_EASE)
            }
        }

        btnUserLocation.setOnClickListener {
            mapOptions.getLastLocation()
                ?.let {
                    launch {
                        mapController.setPositionEased(it, ANIMATION_DURATION)
                        mapController.setZoomEased(17f, ANIMATION_DURATION)
                        repeat(200) {
                            delay(1)
                            resizeAccuracyMarker()
                        }
                    }
                }
        }
    }

    private fun onReCenterStateChanged(recenter: Boolean) {
        when {
            recenter -> {
                mapfitMap.reCenter()
                btnRecenter.setImageResource(R.drawable.mf_re_center)
            }
            else -> btnRecenter.setImageResource(R.drawable.mf_re_center_off)
        }

        reCentered = recenter
    }


    private val onAnnotationClickListener = object : OnAnnotationClickListener {
        override fun onAnnotationClicked(annotation: Annotation) {
            annotation.let {
                if (placeInfoRemoveJob.isActive) placeInfoRemoveJob.cancel()

                when (it) {
                    is Marker -> {
                        markerClickListener?.onMarkerClicked(it)
                        showPlaceInfo(it)
                    }
                    is Polyline -> polylineClickListener?.onPolylineClicked(it)
                    is Polygon -> polygonClickListener?.onPolygonClicked(it)

                    else -> Unit
                }
            }
        }
    }


    private fun hideCompassButton(matrix: Matrix? = null) {
        launch(UI) {
            if (btnCompass.visibility == View.VISIBLE && !animatingCompass) {
                btnCompass.animate().alpha(0f)
                    .setDuration(ANIMATION_DURATION.toLong())
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationEnd(animation: Animator?) {
                            btnCompass.visibility = View.GONE
                            matrix?.let { btnCompass.imageMatrix = it }
                            animatingCompass = false
                        }

                        override fun onAnimationRepeat(animation: Animator?) {}
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}
                    })

                    .start()
                animatingCompass = true
            }
        }
    }

    private fun shoveResponder() = TouchInput.ShoveResponder {
        updatePlaceInfoPosition(false)
        false
    }

    private var scaledMax = false
    private var scaledMin = false
    private var max = false
    private var min = false

    private var scaleFlingJob = Job()

    private fun scaleResponder() = TouchInput.ScaleResponder { _, _, scale, _ ->
        var consumed = false

        scaledMax = mapController.zoom * scale > mapOptions.getMaxZoom() // will exceed
        scaledMin = mapController.zoom * scale < mapOptions.getMinZoom() // will exceed
        max = mapController.zoom >= mapOptions.getMaxZoom() // currently exceeding
        min = mapController.zoom <= mapOptions.getMinZoom() // currently exceeding

        if (scaledMax && max || scaledMin && min) {
            consumed = true
            mapController.zoom = normalizeZoomLevel(mapController.zoom, false)
        }

        if (!consumed) {
            mapPinchListener?.onMapPinch()
        } else {
            updatePlaceInfoPosition(false)
        }

        if (mapOptions.userLocationButtonEnabled) resizeAccuracyMarker()

        launch {
            scaleFlingJob.cancelAndJoin()
            scaleFlingJob = launch {
                delay(700)
                resizeAccuracyMarker()
            }
        }

        consumed
    }

    private fun rotateResponder() = TouchInput.RotateResponder { x, y, rotation ->
        rotateCompassButton()
        false
    }

    private fun resizeAccuracyMarker() {
        if (mapOptions.getUserLocationEnabled()) mapOptions.resizeAccuracyCircle()
    }

    private fun panResponder() = object : TouchInput.PanResponder {
        override fun onPan(startX: Float, startY: Float, endX: Float, endY: Float): Boolean {
            mapPanListener?.onMapPan()

            if (startX != endX && startY != endY) onReCenterStateChanged(false)

            updatePlaceInfoPosition(false)
            return false
        }

        override fun onFling(
            posX: Float,
            posY: Float,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            updatePlaceInfoPosition(true)
            return false
        }
    }

    private fun longClickResponder(): TouchInput.LongPressResponder? {
        return TouchInput.LongPressResponder { x, y ->
            mapLongClickListener?.onMapLongClicked(
                mapController.screenPositionToLatLng(PointF(x, y))
            )
        }
    }

    @JvmSynthetic
    private fun singleTapResponder(): TouchInput.TapResponder {
        return object : TouchInput.TapResponder {
            override fun onSingleTapUp(x: Float, y: Float): Boolean {
                Log.e("onSingleTapUp!!!", "")
                return true
            }

            override fun onSingleTapConfirmed(x: Float, y: Float): Boolean {

                mapController.pickMarker(x, y)
                mapController.pickFeature(x, y)

                mapClickListener?.onMapClicked(mapController.screenPositionToLatLng(PointF(x, y)))
                placeInfoRemoveJob = launch(UI) {
                    delay(20)
                    activePlaceInfo?.dispose()
                }
                return true
            }
        }
    }

    @JvmSynthetic
    private fun doubleTapResponder(): TouchInput.DoubleTapResponder? {
        return TouchInput.DoubleTapResponder { x, y ->
            mapDoubleClickListener?.onMapDoubleClicked(
                mapController.screenPositionToLatLng(PointF(x, y))
            )
            setZoomOnDoubleTap(x, y)
            activePlaceInfo?.updatePositionDelayed()
            resizeAccuracyMarker()
            true
        }
    }

    private fun setZoomOnDoubleTap(x: Float, y: Float) {
        val lngLat = mapController.screenPositionToLatLng(PointF(x, y))
        mapController.setPositionEased(lngLat, ANIMATION_DURATION, DEFAULT_EASE, false)
        mapfitMap.setZoom(mapController.zoom + ZOOM_STEP_LEVEL, ANIMATION_DURATION.toLong())
    }

    @Synchronized
    private fun rotateCompassButton() {
        if (btnCompass.visibility != View.VISIBLE) {
            btnCompass.visibility = View.VISIBLE
            btnCompass.alpha = 1f
        }

        launch {
            val compassRotationMatrix = Matrix()
            compassRotationMatrix.postRotate(
                Math.toDegrees(mapController.rotation.toDouble()).toFloat(),
                compassPivotCenter,
                compassPivotCenter
            )
            btnCompass.imageMatrix = compassRotationMatrix
        }
    }

    internal fun normalizeZoomLevel(zoomLevel: Float, warn: Boolean = true): Float {
        return when {
            zoomLevel > mapOptions.getMaxZoom() -> {
                if (warn) {
                    logWarning(
                        "Zoom level exceeds maximum level and will be set maximum zoom level:  ${mapOptions.getMaxZoom()}"
                    )
                }
                mapOptions.getMaxZoom()
            }
            zoomLevel < mapOptions.getMinZoom() -> {
                if (warn) {
                    logWarning(
                        "Zoom level below minimum level and will be set minimum zoom level:  ${mapOptions.getMinZoom()}"
                    )
                }
                mapOptions.getMinZoom()
            }
            else -> zoomLevel
        }
    }

    private fun showPlaceInfo(marker: Marker) {
        if (marker.hasPlaceInfoFields()) {

            if (activePlaceInfo != null
                && activePlaceInfo?.marker == marker
                && activePlaceInfo?.getVisibility()!!
            ) {
                return
            }

            if (activePlaceInfo != null && activePlaceInfo?.marker != marker) {
                activePlaceInfo?.dispose()
            }

            val view = if (isCustomPlaceInfo()) {
                val view = placeInfoAdapter?.getPlaceInfoView(marker)

                marker.hasCustomPlaceInfo = true

                view?.setOnClickListener {
                    onPlaceInfoClickListener?.onPlaceInfoClicked(marker)
                }

                if (view?.parent != null) {
                    (view.parent as ViewGroup).removeView(view)
                }

                view?.let { placeInfoFrame.addView(view) }
                view

            } else {
                val view =
                    LayoutInflater.from(context)
                        .inflate(R.layout.mf_widget_place_info, placeInfoFrame)

                val child = (view as FrameLayout).getChildAt(0)
                child.tag = "default"
                child.findViewById<View>(R.id.container)
                    .setOnClickListener {
                        onPlaceInfoClickListener?.onPlaceInfoClicked(marker)
                    }
                child
            }

            view?.let {
                it.visibility = View.GONE
                activePlaceInfo = PlaceInfo(it, marker, mapController)
                marker.placeInfoMap[mapController] = activePlaceInfo
                activePlaceInfo?.show()
            }
        }
    }

    private fun isCustomPlaceInfo() = placeInfoAdapter != null

    internal fun updatePlaceInfoPosition(repeating: Boolean) {
        activePlaceInfo
            ?.takeIf { it.getVisibility() }
            ?.let {
                if (!repeating) {
                    activePlaceInfo?.onPositionChanged()
                } else {
                    activePlaceInfo?.updatePositionDelayed()
                }
            }
    }

    private fun getGLSurfaceView(): GLSurfaceView {
        val glSurfaceView = GLSurfaceView(context)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.preserveEGLContextOnPause = true
        glSurfaceView.setEGLConfigChooser(ConfigChooser(8, 8, 8, 0, 16, 8))
        glSurfaceView.id = R.id.glSurface
        addView(glSurfaceView)
        return glSurfaceView
    }

    @TestOnly
    internal fun getMapSnap(callback: (bitmap: Bitmap) -> Unit) {
        mapController.captureFrame(callback, true)
    }

    private fun disposeMap() {
        mapController.dispose()
    }

    /**
     * You must call this method from the parent Activity or Fragment's corresponding method.
     */
    fun onDestroy() {
        disposeMap()
    }

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    fun onLowMemory() {
        mapController.onLowMemory()
    }

    private val mapfitWebsiteIntent by lazy {
        Intent(Intent.ACTION_VIEW, Uri.parse("https://mapfit.com/"))
    }

    private val mapfitLegalIntent by lazy {
        Intent(Intent.ACTION_VIEW, Uri.parse("https://mapfit.com/legalnotices"))
    }

    private var attributionAnimJob: Job? = null

    private fun getHttpHandler(): HttpHandler {
        val cacheDir = context.cacheDir
        if (cacheDir != null && cacheDir.exists()) {
            val cachePolicy = object : CachePolicy {
                internal var tileCacheControl =
                    CacheControl.Builder().maxStale(7, TimeUnit.DAYS).build()

                override fun apply(url: HttpUrl): CacheControl? {
                    return when (url.host()) {
                        "tiles2.mapfit.com",
                        "cdn.mapfit.com" -> tileCacheControl
                        else -> null
                    }
                }
            }
            return HttpHandler(
                File(cacheDir, "tile_cache"),
                (30 * 1024 * 1024).toLong(),
                cachePolicy
            )
        }
        return HttpHandler()
    }

    @TestOnly
    internal fun getScreenPosition(latLng: LatLng) = mapController.latLngToScreenPosition(latLng)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewHeight = h
        viewWidth = w
    }

}