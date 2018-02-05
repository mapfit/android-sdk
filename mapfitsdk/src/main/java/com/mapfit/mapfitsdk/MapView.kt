package com.mapfit.mapfitsdk

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
import com.mapfit.mapfitsdk.annotations.Marker
import com.mapfit.mapfitsdk.annotations.Polygon
import com.mapfit.mapfitsdk.annotations.Polyline
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerAddedCallback
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerClickListener
import com.mapfit.mapfitsdk.annotations.callback.OnPolygonClickListener
import com.mapfit.mapfitsdk.annotations.callback.OnPolylineClickListener
import com.mapfit.mapfitsdk.annotations.widget.PlaceInfo
import com.mapfit.mapfitsdk.geocoder.GeocoderApi
import com.mapfit.mapfitsdk.geocoder.GeocoderCallback
import com.mapfit.mapfitsdk.geocoder.model.Address
import com.mapfit.mapfitsdk.geometry.LatLng
import com.mapfit.mapfitsdk.geometry.LatLngBounds
import com.mapfit.mapfitsdk.geometry.isEmpty
import com.mapfit.mapfitsdk.utils.isValidZoomLevel
import com.mapfit.mapfitsdk.utils.startActivitySafe
import com.mapfit.tangram.ConfigChooser
import com.mapfit.tangram.MarkerPickResult
import com.mapfit.tangram.TouchInput
import kotlinx.android.synthetic.main.overlay_map_controls.view.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.annotations.NotNull
import java.io.IOException


/**
 * The view map is drawn onto.
 *
 * Created by dogangulcan on 12/18/17.
 */
class MapView(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private val ANIMATION_DURATION = 200
    private val ZOOM_STEP_LEVEL = 1

    private lateinit var mapController: MapController
    private lateinit var mapOptions: MapOptions
    private val geocoder by lazy { GeocoderApi() }

    // Views
    private val controlsView: View by lazy {
        LayoutInflater.from(context)
            .inflate(R.layout.overlay_map_controls, this, false)
    }

    private val placeInfoFrame = FrameLayout(context)

    private val attributionImage: ImageView = controlsView.findViewById(R.id.imgAttribution)

    @JvmSynthetic
    internal fun getAttributionImage(): ImageView = attributionImage

    internal val zoomControlsView: RelativeLayout by lazy {
        controlsView.findViewById<RelativeLayout>(R.id.zoomControls)
    }

    internal val btnRecenter: View by lazy {
        controlsView.findViewById<View>(R.id.btnRecenter)
    }

    internal val btnCompass: View by lazy {
        controlsView.findViewById<View>(R.id.btnCompass)
    }

    private val annotationLayer = Layer()

    private val layers = mutableListOf(annotationLayer)

    // Click Listeners
    private var markerClickListener: OnMarkerClickListener? = null
    private var mapClickListener: OnMapClickListener? = null
    private var mapDoubleClickListener: OnMapDoubleClickListener? = null
    private var mapLongClickListener: OnMapLongClickListener? = null
    private var mapPanListener: OnMapPanListener? = null
    private var mapPinchListener: OnMapPinchListener? = null
    private var placeInfoAdapter: MapfitMap.PlaceInfoAdapter? = null
    private var onPlaceInfoClickListener: MapfitMap.OnPlaceInfoClickListener? = null

    private var viewHeight: Int? = null
    private var viewWidth: Int? = null

    private var placeInfoRemoveJob = Job()


    init {
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
            mapfitMap.setZoom(mapfitMap.getZoom() + ZOOM_STEP_LEVEL, ANIMATION_DURATION)
        }

        btnZoomOut.setOnClickListener {
            mapfitMap.setZoom(mapfitMap.getZoom() - ZOOM_STEP_LEVEL, ANIMATION_DURATION)
        }

        btnRecenter.setOnClickListener {
            mapfitMap.reCenter()
        }

        btnCompass.setOnClickListener {
            mapController.setRotationEased(0f, ANIMATION_DURATION, MapController.EaseType.CUBIC)
        }

//        zoomControls.setBackgroundResource(R.drawable.zoom_buttons_bg)

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

            setMarkerPickListener(markerPickListener())

            setSceneLoadListener({ _, _ ->
                onMapReadyCallback.onMapReady(mapfitMap)
            })

            mapOptions = MapOptions(this@MapView, this)
            mapOptions.theme = mapTheme

        }
    }

    private fun shoveResponder() = TouchInput.ShoveResponder {
        updatePlaceInfoPosition(false)
        false
    }

    private fun scaleResponder() = TouchInput.ScaleResponder { x, y, scale, velocity ->
        var consumed = false
        mapPinchListener?.let {
            it.onMapPinch()
            consumed = true
        }
        updatePlaceInfoPosition(false)

        consumed
    }

    private fun panResponder() = object : TouchInput.PanResponder {
        override fun onPan(startX: Float, startY: Float, endX: Float, endY: Float): Boolean {
            var consumed = false
            mapPanListener?.let {
                it.onMapPan()
                consumed = true
            }

            updatePlaceInfoPosition(false)
            return consumed
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
                mapController.screenPositionToLatLng(
                    PointF(
                        x,
                        y
                    )
                )
            )
            setZoomOnDoubleTap(x, y)
            activePlaceInfo?.updatePositionDelayed()
            true
        }
    }

    private fun markerPickListener(): (MarkerPickResult?, Float, Float) -> Unit {
        return { markerPickResult, _, _ ->

            runBlocking {
                markerPickResult?.let {
                    val annotation = annotationLayer.annotations.find {
                        it.getId() == markerPickResult.marker.getId()
                    }

                    annotation?.let {

                        if (placeInfoRemoveJob.isActive) placeInfoRemoveJob.cancel()

                        when (it) {
                            is Marker -> {
                                markerClickListener?.onMarkerClicked(it)
                                showPlaceInfo(it, mapController)
                            }
                            else -> {
                            }
                        }

                    }
                }
            }
        }
    }

    private fun setZoomOnDoubleTap(x: Float, y: Float) {
        val lngLat = mapController.screenPositionToLatLng(PointF(x, y))
        mapController.setPositionEased(lngLat, ANIMATION_DURATION)
        mapController.setZoomEased(mapController.zoom + ZOOM_STEP_LEVEL, ANIMATION_DURATION)
    }

    private val mapfitMap = object : MapfitMap() {
        override fun setOnPlaceInfoClickListener(listener: OnPlaceInfoClickListener) {
            this@MapView.onPlaceInfoClickListener = listener
        }

        override fun setPlaceInfoAdapter(adapter: PlaceInfoAdapter) {
            this@MapView.placeInfoAdapter = adapter
        }

        override fun setOnMapPinchListener(listener: OnMapPinchListener) {
            mapPinchListener = listener
        }

        override fun addMarker(address: String, onMarkerAddedCallback: OnMarkerAddedCallback) {
            geocoder.geocodeAddress(address, object : GeocoderCallback {
                override fun onSuccess(addressList: List<Address>) {

                    var latLng = LatLng()
                    addressList.forEach { address ->
                        latLng = if (address.entrances.isNotEmpty()) {
                            LatLng(
                                address.entrances.first().latitude,
                                address.entrances.first().longitude
                            )
                        } else {
                            LatLng(address.latitude, address.longitude)
                        }
                    }

                    if (latLng.isEmpty()) {
                        onMarkerAddedCallback.onError(IOException("No coordinates found for given address."))
                    } else {
                        val marker = mapController.addMarker().setPosition(latLng)
                        marker.address = addressList[0]
                        annotationLayer.add(marker)
                        onMarkerAddedCallback.onMarkerAdded(marker)
                    }
                }

                override fun onError(message: String, e: Exception) {
                    onMarkerAddedCallback.onError(e)
                }
            })
        }

        override fun addMarker(latLng: LatLng): Marker {
            val marker = mapController.addMarker()
            marker.setPosition(latLng)
            annotationLayer.add(marker)
            return marker
        }

        override fun addPolyline(line: List<LatLng>): Polyline {
            val polyline = mapController.addPolyline(line)
            annotationLayer.add(polyline)
            return polyline
        }

        override fun getLayers(): List<Layer> {
            return layers
        }

        override fun setCenterWithLayer(layer: Layer, duration: Long, paddingPercentage: Float) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setBounds(bounds: LatLngBounds, padding: Float) {
            mapController.setLatlngBounds(bounds, padding)
        }

        override fun getBounds(): LatLngBounds {
            val sw = mapController.screenPositionToLatLng(PointF(0f, viewHeight?.toFloat() ?: 0f))
            val ne = mapController.screenPositionToLatLng(PointF(viewWidth?.toFloat() ?: 0f, 0f))
            return LatLngBounds(ne, sw)
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

        override fun setOnPolylineClickListener(listener: OnPolylineClickListener) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setOnPolygonClickListener(listener: OnPolygonClickListener) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getDirectionsOptions(): DirectionsOptions {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setTilt(angle: Float) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getTilt(): Float {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setRotation(angle: Float) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getRotation(): Float {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setOnMarkerClickListener(listener: OnMarkerClickListener) {
            this@MapView.markerClickListener = listener
        }

        override fun addPolygon(polygon: List<List<LatLng>>): Polygon {

            val poly = polygon.map {
                it.map {
                    LatLng(it.lat, it.lon)
                }
            }

            val tMarker = mapController.addMarker()
//            tMarker.setPolygon(com.mapfit.tangram.geometry.Polygon(poly, null))

            return Polygon(tMarker)
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
                    MapController.EaseType.CUBIC
                )
            }
        }

        override fun getCenter(): LatLng = mapController.position

        override fun reCenter() {
            mapController.reCenter()
        }

        override fun addLayer(layer: Layer) {
            layers.add(layer)
            annotationLayer.annotations.addAll(layer.annotations)
            layer.addMap(mapController)
        }

        override fun removeLayer(layer: Layer) {
            layer.annotations.forEach {
                it.mapBindings[mapController]?.let { id ->

                    if (it is Marker && activePlaceInfo?.marker == it) {
                        activePlaceInfo?.dispose(true)
                    }
                    mapController.removeMarker(id)
                }
            }

            annotationLayer.annotations.removeAll(layer.annotations)
            layers.remove(layer)
        }

        override fun removeMarker(marker: Marker): Boolean = marker.remove(mapController)

        override fun removePolygon(polygon: Polygon) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun removePolyline(polyline: Polyline) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getMapOptions(): MapOptions {
            return mapOptions
        }

        override fun setZoom(zoomLevel: Float, duration: Int) {
            if (isValidZoomLevel(zoomLevel)) {
                when (zoomLevel) {
                    !in 1F..mapOptions.getMaxZoom() -> {
                        Log.w("MapView", "Zoom level exceeds maximum level.")
                    }
                    else -> {
                        if (duration == 0) {
                            mapController.zoom = (zoomLevel)
                        } else {
                            mapController.setZoomEased(zoomLevel, duration)
                        }
                    }
                }
            }
        }
    }

    fun getPlaceInfoAdapter(): MapfitMap.PlaceInfoAdapter? = placeInfoAdapter

    private var activePlaceInfo: PlaceInfo? = null

    private fun showPlaceInfo(
        marker: Marker,
        mapController: MapController
    ) {
        if (marker.hasPlaceInfoFields()) {

            if (activePlaceInfo != null
                && activePlaceInfo?.marker == marker
                && activePlaceInfo?.getVisible()!!) {
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
                    LayoutInflater.from(context).inflate(R.layout.widget_place_info, placeInfoFrame)

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

    private fun updatePlaceInfoPosition(repeating: Boolean) {
        if (!repeating) {
            activePlaceInfo?.onPositionChanged()
        } else {
            activePlaceInfo?.updatePositionDelayed()
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