package com.mapfit.android

import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatDelegate
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.mapfit.android.annotations.*
import com.mapfit.android.annotations.Annotation
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback
import com.mapfit.android.annotations.callback.OnMarkerClickListener
import com.mapfit.android.annotations.callback.OnPolygonClickListener
import com.mapfit.android.annotations.callback.OnPolylineClickListener
import com.mapfit.android.annotations.widget.PlaceInfo
import com.mapfit.android.geocoder.Geocoder
import com.mapfit.android.geocoder.GeocoderCallback
import com.mapfit.android.geocoder.model.Address
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.LatLngBounds
import com.mapfit.android.utils.isEmpty
import com.mapfit.android.utils.startActivitySafe
import com.mapfit.tetragon.ConfigChooser
import com.mapfit.tetragon.TouchInput
import kotlinx.android.synthetic.main.mf_overlay_map_controls.view.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.TestOnly
import java.io.IOException


/**
 * The view map is drawn on to.
 *
 * Created by dogangulcan on 12/18/17.
 */
class MapView(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private val ANIMATION_DURATION = 200
    private val ZOOM_STEP_LEVEL = 1
    private val DEFAULT_EASE = MapController.EaseType.CUBIC

    private lateinit var mapController: MapController
    private lateinit var mapOptions: MapOptions
    private lateinit var directionsOptions: DirectionsOptions
    private val geocoder = Geocoder()

    // Views
    private val controlsView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.mf_overlay_map_controls, this, false)
    }

    private val placeInfoFrame = FrameLayout(context)

    private val attributionImage: ImageView = controlsView.findViewById(R.id.imgAttribution)

    internal val zoomControlsView: RelativeLayout by lazy {
        controlsView.findViewById<RelativeLayout>(R.id.zoomControls)
    }

    internal val btnRecenter: ImageView by lazy {
        controlsView.findViewById<ImageView>(R.id.btnRecenter)
    }

    internal val btnCompass: View by lazy {
        controlsView.findViewById<View>(R.id.btnCompass)
    }

    @JvmSynthetic
    internal fun getAttributionImage(): ImageView = attributionImage

    private val layers = mutableListOf<Layer>()

    // event listeners
    private var markerClickListener: OnMarkerClickListener? = null
    private var polylineClickListener: OnPolylineClickListener? = null
    private var polygonClickListener: OnPolygonClickListener? = null
    private var mapClickListener: OnMapClickListener? = null
    private var mapDoubleClickListener: OnMapDoubleClickListener? = null
    private var mapLongClickListener: OnMapLongClickListener? = null
    private var mapPanListener: OnMapPanListener? = null
    private var mapThemeLoadListener: OnMapThemeLoadListener? = null
    private var mapPinchListener: OnMapPinchListener? = null
    private var placeInfoAdapter: MapfitMap.PlaceInfoAdapter? = null
    private var onPlaceInfoClickListener: MapfitMap.OnPlaceInfoClickListener? = null

    private var viewHeight: Int? = null
    private var viewWidth: Int? = null

    private var activePlaceInfo: PlaceInfo? = null
    private var placeInfoRemoveJob = Job()
    private var reCentered = false
    private var sceneUpdateFlag = false

    init {

        Mapfit.getApiKey()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        post {
            viewHeight = height
            viewWidth = width
        }
    }

    @JvmOverloads
    fun getMapAsync(mapTheme: MapTheme = MapTheme.MAPFIT_DAY, @NotNull onMapReadyCallback: OnMapReadyCallback) {
        if (::mapController.isInitialized) {
            onMapReadyCallback.onMapReady(mapfitMap)
        }

        initMapController(mapTheme, onMapReadyCallback)
        initUiControls()
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

        btnLegal.setOnClickListener({
            context.startActivitySafe(mapfitLegalIntent)
        })

        btnBuildYourMap.setOnClickListener({
            context.startActivitySafe(mapfitWebsiteIntent)
        })

        btnZoomIn.setOnClickListener {
            mapfitMap.setZoom(mapfitMap.getZoom() + ZOOM_STEP_LEVEL, ANIMATION_DURATION.toLong())
        }

        btnZoomOut.setOnClickListener {
            mapfitMap.setZoom(mapfitMap.getZoom() - ZOOM_STEP_LEVEL, ANIMATION_DURATION.toLong())
        }

        btnRecenter.setOnClickListener {
            onReCenterStateChanged(true)
        }

        btnCompass.setOnClickListener {
            mapController.setRotationEased(0f, ANIMATION_DURATION, DEFAULT_EASE)
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

    private fun initMapController(mapTheme: MapTheme, onMapReadyCallback: OnMapReadyCallback) {
        mapController = MapController(getGLSurfaceView())

        mapController.apply {
            init()

            setTapResponder(singleTapResponder())
            setDoubleTapResponder(doubleTapResponder())
            setLongPressResponder(longClickResponder())
            setPanResponder(panResponder())
            setScaleResponder(scaleResponder())
            setShoveResponder(shoveResponder())

            setAnnotationClickListener(onAnnotationClickListener)

            setSceneLoadListener { sceneId, sceneError ->
                mapController.reAddMarkers()
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
            mapOptions.theme = mapTheme

        }
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

    private fun shoveResponder() = TouchInput.ShoveResponder {
        updatePlaceInfoPosition(false)
        false
    }

    private var scaledMax = false
    private var scaledMin = false
    private var max = false
    private var min = false
    private fun scaleResponder() = TouchInput.ScaleResponder { x, y, scale, velocity ->
        var consumed = false

        scaledMax = mapController.zoom * scale > mapOptions.getMaxZoom() // will exceed
        scaledMin = mapController.zoom * scale < mapOptions.getMinZoom() // will exceed
        max = mapController.zoom >= mapOptions.getMaxZoom() // currently exceeding
        min = mapController.zoom <= mapOptions.getMinZoom() // currently exceeding

        if (scaledMax && max || scaledMin && min) {
            consumed = true
            mapfitMap.setZoom(mapController.zoom, 125)
        }

        if (!consumed) {
            mapPinchListener?.onMapPinch()
        } else {
            updatePlaceInfoPosition(false)
        }

        consumed
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
            true
        }
    }

    private fun setZoomOnDoubleTap(x: Float, y: Float) {
        val lngLat = mapController.screenPositionToLatLng(PointF(x, y))
        mapController.setPositionEased(lngLat, ANIMATION_DURATION, DEFAULT_EASE, false)
        mapfitMap.setZoom(mapController.zoom + ZOOM_STEP_LEVEL, ANIMATION_DURATION.toLong())
    }

    private val mapfitMap = object : MapfitMap() {
        override fun setOnPolygonClickListener(listener: OnPolygonClickListener) {
            polygonClickListener = listener
        }

        override fun setOnPolylineClickListener(listener: OnPolylineClickListener) {
            polylineClickListener = listener
        }

        override fun setOnMapThemeLoadListener(listener: OnMapThemeLoadListener) {
            mapThemeLoadListener = listener
        }

        override fun getTilt(): Float = mapController.tilt

        override fun setTilt(angle: Float, duration: Long) {
            mapController.setTiltEased(angle, duration, DEFAULT_EASE)
        }

        override fun setTilt(angle: Float) {
            mapController.tilt = angle
        }

        override fun setRotation(rotation: Float, duration: Long) {
            mapController.setRotationEased(rotation, duration.toInt(), DEFAULT_EASE)
        }

        override fun setRotation(rotation: Float) {
            mapController.rotation = rotation
        }

        override fun getRotation(): Float = mapController.rotation

        @TestOnly
        override fun has(annotation: Annotation): Boolean = mapController.contains(annotation)

        override fun setOnPlaceInfoClickListener(listener: OnPlaceInfoClickListener) {
            this@MapView.onPlaceInfoClickListener = listener
        }

        override fun setPlaceInfoAdapter(adapter: PlaceInfoAdapter) {
            this@MapView.placeInfoAdapter = adapter
        }

        override fun setOnMapPinchListener(listener: OnMapPinchListener) {
            mapPinchListener = listener
        }

        override fun addMarker(
            address: String,
            withBuilding: Boolean,
            onMarkerAddedCallback: OnMarkerAddedCallback
        ) {
            geocoder.geocode(address,
                withBuilding,
                object : GeocoderCallback {
                    override fun onSuccess(addressList: List<Address>) {

                        var latLng = LatLng()
                        addressList.forEach { address ->
                            latLng = address.getPrimaryEntrance()
                        }

                        if (latLng.isEmpty()) {
                            onMarkerAddedCallback.onError(IOException("No coordinates found for given address."))
                        } else {
                            val marker = mapController.addMarker().setPosition(latLng)
//
                            marker.address = addressList[0]
                            if (withBuilding) {
                                val polygon =
                                    mapController.addPolygon(addressList[0].building.polygon)
                                marker.setPolygon(polygon)
                            }
                            async(UI) {
                                onMarkerAddedCallback.onMarkerAdded(marker)
                            }
                        }
                    }

                    override fun onError(message: String, e: Exception) {
                        async(UI) {
                            onMarkerAddedCallback.onError(e)
                        }
                    }
                })
        }

        override fun addMarker(latLng: LatLng): Marker {
            val marker = mapController.addMarker()
            marker.setPosition(latLng)
            return marker
        }

        override fun addPolyline(line: List<LatLng>): Polyline {
            return mapController.addPolyline(line)
        }

        override fun addPolygon(polygon: List<List<LatLng>>): Polygon {
            return mapController.addPolygon(polygon)
        }

        override fun getLayers(): List<Layer> {
            return layers
        }

        override fun setCenterWithLayer(layer: Layer) {
            mapController.position = layer.getLatLngBounds().center
            updatePlaceInfoPosition(true)
        }

        override fun setLatLngBounds(bounds: LatLngBounds, padding: Float) {
            mapController.setLatLngBounds(bounds, padding)
            updatePlaceInfoPosition(true)
        }

        override fun getLatLngBounds(): LatLngBounds {
            val sw = mapController.screenPositionToLatLng(PointF(0f, viewHeight?.toFloat() ?: 0f))
            val ne = mapController.screenPositionToLatLng(PointF(viewWidth?.toFloat() ?: 0f, 0f))
            return LatLngBounds(ne ?: LatLng(), sw ?: LatLng())
        }

        override fun setOnMapClickListener(listener: OnMapClickListener) {
            mapClickListener = listener
        }

        override fun setOnMapDoubleClickListener(listener: OnMapDoubleClickListener) {
            mapDoubleClickListener = listener
        }

        override fun setOnMapLongClickListener(listener: OnMapLongClickListener) {
            mapLongClickListener = listener
        }

        override fun setOnMapPanListener(listener: OnMapPanListener) {
            mapPanListener = listener
        }

        override fun getDirectionsOptions(): DirectionsOptions = directionsOptions


        override fun setOnMarkerClickListener(listener: OnMarkerClickListener) {
            markerClickListener = listener
        }

        override fun getZoom(): Float {
            return mapController.zoom
        }

        override fun setCenter(latLng: LatLng) {
            setCenter(latLng, 0)
        }

        override fun setCenter(latLng: LatLng, duration: Long) {
            if (duration.toInt() == 0) {
                mapController.position = latLng

            } else {
                mapController.setPositionEased(
                    latLng,
                    duration.toInt(),
                    DEFAULT_EASE,
                    true
                )
            }
            updatePlaceInfoPosition(true)
        }

        override fun getCenter(): LatLng = mapController.position

        override fun reCenter() {
            mapController.reCenter()
            updatePlaceInfoPosition(true)
        }

        override fun addLayer(layer: Layer) {
            if (!layers.contains(layer)) {
                layers.add(layer)
                layer.addMap(mapController)
            }
        }

        override fun removeLayer(layer: Layer) {
            layer.annotations.forEach {
                it.mapBindings[mapController]?.let { id ->
                    if (it is Marker && activePlaceInfo?.marker == it) {
                        activePlaceInfo?.dispose(true)
                    }
                    mapController.removeMarker(id)
                    it.mapBindings.remove(mapController)
                }
            }

            layers.remove(layer)
        }

        override fun removeMarker(marker: Marker) {
            marker.remove(mapController)
        }

        override fun removePolygon(polygon: Polygon) {
            polygon.remove(mapController)
        }

        override fun removePolyline(polyline: Polyline) {
            polyline.remove(mapController)
        }

        override fun getMapOptions(): MapOptions {
            return mapOptions
        }


        override fun setZoom(zoomLevel: Float) {
            setZoom(zoomLevel, 0)
        }

        override fun setZoom(zoomLevel: Float, duration: Long) {
            val normalizedZoomLevel = normalizeZoomLevel(zoomLevel)

            if (duration.toInt() == 0) {
                mapController.zoom = (normalizedZoomLevel)
            } else {
                mapController.setZoomEased(normalizedZoomLevel, duration.toInt())
            }
        }
    }

    private fun normalizeZoomLevel(zoomLevel: Float): Float =
        when {
            zoomLevel > mapOptions.getMaxZoom() -> {
                Log.w(
                    "MapView",
                    "Zoom level exceeds maximum level and will be set maximum zoom level:  ${mapOptions.getMaxZoom()}"
                )
                mapOptions.getMaxZoom()
            }
            zoomLevel < mapOptions.getMinZoom() -> {
                Log.w(
                    "MapView",
                    "Zoom level below minimum level and will be set minimum zoom level:  ${mapOptions.getMinZoom()}"
                )
                mapOptions.getMinZoom()
            }
            else -> zoomLevel
        }

    private fun showPlaceInfo(marker: Marker) {
        if (marker.hasPlaceInfoFields()) {

            if (activePlaceInfo != null
                && activePlaceInfo?.marker == marker
                && activePlaceInfo?.getVisibility()!!) {
                return
            }

            if (activePlaceInfo != null && activePlaceInfo?.marker != marker) {
                activePlaceInfo?.dispose()
            }

            val view = if (isCustomPlaceInfo()) {
                val view = placeInfoAdapter?.getPlaceInfoView(marker)

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
                marker.placeInfoMap.put(mapController, activePlaceInfo)
                activePlaceInfo?.show()
            }
        }
    }

    private fun isCustomPlaceInfo() = placeInfoAdapter != null

    private fun updatePlaceInfoPosition(repeating: Boolean) {
        activePlaceInfo
            ?.takeIf { it.getVisibility() }
            .let {
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


}