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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.mapfit.mapfitsdk.annotations.Marker
import com.mapfit.mapfitsdk.annotations.Polygon
import com.mapfit.mapfitsdk.annotations.Polyline
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerAddedCallback
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerClickListener
import com.mapfit.mapfitsdk.annotations.callback.OnPolygonClickListener
import com.mapfit.mapfitsdk.annotations.callback.OnPolylineClickListener
import com.mapfit.mapfitsdk.geocoder.Geocoder
import com.mapfit.mapfitsdk.geocoder.GeocoderCallback
import com.mapfit.mapfitsdk.geocoder.model.Address
import com.mapfit.mapfitsdk.geometry.LatLng
import com.mapfit.mapfitsdk.geometry.LatLngBounds
import com.mapfit.mapfitsdk.utils.isEmpty
import com.mapfit.mapfitsdk.utils.isValidZoomLevel
import com.mapfit.mapfitsdk.utils.startActivitySafe
import com.mapfit.tangram.ConfigChooser
import com.mapfit.tangram.MarkerPickResult
import com.mapfit.tangram.TouchInput
import kotlinx.android.synthetic.main.overlay_map_controls.view.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
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
) : RelativeLayout(context, attributeSet) {

    private val ANIMATION_DURATION = 200
    private val ZOOM_STEP_LEVEL = 1

    private lateinit var mapController: MapController
    private lateinit var mapOptions: MapOptions
    private val geocoder by lazy { Geocoder() }


    // Views
    private val controlsView: View by lazy {
        LayoutInflater.from(context)
                .inflate(R.layout.overlay_map_controls, this, false)
    }

    private val attributionImage: ImageView = controlsView.findViewById(R.id.imgAttribution)

    @JvmSynthetic
    internal fun getAttributionImage(): ImageView = attributionImage

    private val zoomControlsView: LinearLayout by lazy {
        controlsView.findViewById<LinearLayout>(R.id.zoomControls)
    }

    private val annotationLayer = Layer()
    private val layers = mutableListOf(annotationLayer)

    private var tilt: Float = 0f
    private var isDirectionsEnabled = false
    private lateinit var directionsOptions: DirectionsOptions
    private val directionsView: View by lazy { getDirectionView() }

    private lateinit var mapCenter: LatLng
    private var isUserLocationEnabled = true

    private val dataLayers = mutableListOf<MapData>()

    // Click Listeners
    private var markerClickListener: OnMarkerClickListener? = null
    private var polylineClickListener: OnPolylineClickListener? = null
    private var polygonClickListener: OnPolygonClickListener? = null
    private var mapClickListener: OnMapClickListener? = null
    private var mapDoubleClickListener: OnMapDoubleClickListener? = null

    private var viewHeight: Int? = null
    private var viewWidth: Int? = null

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
//

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
    }

    private fun initMapController(mapTheme: MapTheme, onMapReadyCallback: OnMapReadyCallback) {
        mapController = MapController(getGLSurfaceView())
        mapController.apply {
            init()
            setTapResponder(singleTapResponder())
            setDoubleTapResponder(doubleTapResponder())
            setMarkerPickListener(markerPickListener())
            setSceneLoadListener({ _, _ ->
                onMapReadyCallback.onMapReady(mapfitMap)
            })

            mapOptions = MapOptions(this@MapView, this)
            mapOptions.setTheme(mapTheme)

        }
    }

    @JvmSynthetic
    internal fun singleTapResponder(): TouchInput.TapResponder {
        return object : TouchInput.TapResponder {
            override fun onSingleTapUp(x: Float, y: Float): Boolean {
                Log.e("onSingleTapUp!!!", "")
                return true
            }

            override fun onSingleTapConfirmed(x: Float, y: Float): Boolean {
                mapController.pickMarker(x, y)
                mapClickListener?.onMapClicked(mapController.screenPositionToLatLng(PointF(x, y)))
                return true
            }
        }
    }

    @JvmSynthetic
    internal fun doubleTapResponder(): TouchInput.DoubleTapResponder? {
        return TouchInput.DoubleTapResponder { x, y ->
            mapDoubleClickListener?.onMapDoubleClicked(mapController.screenPositionToLatLng(PointF(x, y)))
            setZoomOnDoubleTap(x, y)
            true
        }
    }

    private fun markerPickListener(): (MarkerPickResult?, Float, Float) -> Unit {
        return { markerPickResult, _, _ ->

            markerPickResult?.run {
                //                val annotation = annotationLayer.getAnnotations().find {
//                    it.getId() == markerPickResult.marker
//                }

//                annotation?.let {
//                    when (it) {
//                        is Marker -> markerClickListener?.onMarkerClicked(it)
//                        is Polyline -> {
//                        }
//                        else -> {
//                        }
//                    }
//
//                }
            }
        }
    }

    private fun setZoomOnDoubleTap(x: Float, y: Float) {
        val lngLat = mapController.screenPositionToLatLng(PointF(x, y))
        mapController.setPositionEased(lngLat, ANIMATION_DURATION)
        mapController.setZoomEased(mapController.zoom + ZOOM_STEP_LEVEL, ANIMATION_DURATION)
    }

    private val mapfitMap = object : MapfitMap() {
        override fun setIsTiltEnabled(isEnabled: Boolean) {
            mapController.touchInput.tiltEnabled = isEnabled
        }

        override fun setIsRotateEnabled(isEnabled: Boolean) {
            mapController.touchInput.rotationEnabled = isEnabled

        }

        override fun setIsPanEnabled(isEnabled: Boolean) {
            mapController.touchInput.panEnabled = isEnabled
        }

        override fun setIsPinchEnabled(isEnabled: Boolean) {
            mapController.touchInput.pinchEnabled = isEnabled
        }

        override fun getIsTiltEnabled(): Boolean = mapController.touchInput.tiltEnabled

        override fun getIsRotateEnabled(): Boolean = mapController.touchInput.rotationEnabled

        override fun getIsPanEnabled(): Boolean = mapController.touchInput.panEnabled

        override fun getIsPinchEnabled(): Boolean = mapController.touchInput.pinchEnabled

        override fun addMarker(address: String, onMarkerAddedCallback: OnMarkerAddedCallback) {
            geocoder.geocodeAddress(address, object : GeocoderCallback {
                override fun onSuccess(addressList: List<Address>) {

                    var latLng = LatLng()
                    addressList.forEach { address ->
                        latLng = if (address.entrances.isNotEmpty()) {
                            LatLng(address.entrances.first().latitude,
                                    address.entrances.first().longitude)
                        } else {
                            LatLng(address.latitude, address.longitude)
                        }
                    }

                    if (latLng.isEmpty()) {
                        onMarkerAddedCallback.onError(IOException("No coordinates found for given address."))
                    } else {
                        val marker = mapController.addMarker().setPosition(latLng)
                        onMarkerAddedCallback.onMarkerAdded(marker)
                    }
                }

                override fun onError(message: String, e: Exception) {
                    onMarkerAddedCallback.onError(e)
                }
            })
        }

        override fun getLayers(): List<Layer> {
            return layers
        }

        override fun setCenterWithLayer(layer: Layer, duration: Long, paddingPercentage: Float) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setBounds(latLngBounds: LatLngBounds) {
            mapController.setLatlngBounds(latLngBounds, 100)
        }

        override fun getBounds(): LatLngBounds {
            val sw = mapController.screenPositionToLatLng(PointF(0f, viewHeight?.toFloat() ?: 0f))
            val ne = mapController.screenPositionToLatLng(PointF(viewWidth?.toFloat() ?: 0f, 0f))
            return LatLngBounds(ne, sw)
        }

        override fun setOnMapClickListener(onMapClickListener: OnMapClickListener) {
            mapClickListener = onMapClickListener
        }

        override fun setOnMapDoubleClickListener(onMapDoubleClickListener: OnMapDoubleClickListener) {
            mapDoubleClickListener = onMapDoubleClickListener
        }

        override fun setOnPolylineClickListener(onPolylineClickListener: OnPolylineClickListener) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setOnPolygonClickListener(onPolygonClickListener: OnPolygonClickListener) {
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

        override fun setOnMarkerClickListener(onMarkerClickListener: OnMarkerClickListener) {
            this@MapView.markerClickListener = onMarkerClickListener
        }

        override fun addMarker(latLng: LatLng): Marker {
            val marker = mapController.addMarker()
            marker.setPosition(latLng)
            annotationLayer.add(marker)
            return marker
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

        override fun addPolyline(): Polyline {
            return Polyline()
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

        override fun getCenter(): LatLng {
            val position = mapController.position
            return LatLng(position.lat, position.lon)
        }

        override fun reCenter(duration: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addLayer(layer: Layer) {
//            layer.bindTo(mapController)
            layers.add(layer)
        }

        override fun removeLayer(layer: Layer) {
            layers.remove(layer)
        }

        override fun removeMarker(marker: Marker): Boolean =
                mapController.removeMarker(marker)

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

//    private fun screenPositionToLatLng(screenPosition: PointF): LatLng {
//        val lngLat = mapController.screenPositionToLatLng(PointF(screenPosition.x, screenPosition.y))
//        return LatLng(lngLat.latitude, lngLat.longitude)
//    }

    @JvmSynthetic
    internal fun setZoomControlVisibility(visible: Boolean) {
        zoomControlsView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun getDirectionView(): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getGLSurfaceView(): GLSurfaceView {
        val glSurfaceView = GLSurfaceView(context)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.preserveEGLContextOnPause = true
        glSurfaceView.setEGLConfigChooser(ConfigChooser(8, 8, 8, 0, 16, 8))

        addView(glSurfaceView)
        return glSurfaceView
    }

    private fun disposeMap() {

        if (mapController != null) {
            // MapController has been initialized, so we'll dispose it now.
            mapController.dispose()
        }
//        mapController = null

    }

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     * Any access to MapView.mapController is illegal after this call.
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