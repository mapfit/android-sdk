package com.mapfit.mapfitsdk

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.mapfit.mapfitsdk.annotations.*
import com.mapfit.mapfitsdk.annotations.Marker
import com.mapfit.mapfitsdk.annotations.callback.*
import com.mapfit.mapfitsdk.geo.LatLng
import com.mapfit.mapfitsdk.geo.LatLngBounds
import com.mapfit.mapfitsdk.utils.isValidZoomLevel
import com.mapzen.tangram.*
import com.mapzen.tangram.MapView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async


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

    private lateinit var tangramMap: com.mapzen.tangram.MapController
    private lateinit var mapOptions: MapOptions

    private val annotationLayer = Layer()
    private var tilt: Float = 0f
    private var isDirectionsEnabled = false
    private lateinit var directionsOptions: DirectionsOptions
    private val directionsView: View by lazy { getDirectionView() }

    private lateinit var mapCenter: LatLng
    private var isUserLocationEnabled = true

    private val controlsView: View by lazy { getUiControlView() }
    private val tangramMapView by lazy { MapView(context, attributeSet) }

    private val dataLayers = mutableListOf<MapData>()

    private var markerClickListener: OnMarkerClickListener? = null
    private var polylineClickListener: OnPolylineClickListener? = null
    private var polygonClickListener: OnPolygonClickListener? = null
    private var mapClickListener: OnMapClickListener? = null
    private var mapDoubleClickListener: OnMapDoubleClickListener? = null

    private val zoomControlsView: LinearLayout

    private val layers = mutableListOf(annotationLayer)

    init {
        addView(tangramMapView)
        addView(controlsView)

        zoomControlsView = controlsView.findViewById(R.id.zoomControls)
        controlsView.findViewById<View>(R.id.imgAttribution).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mapfit.com/"))

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Log.d(TAG, "No Intent available to handle opening https://mapfit.com/")
            }
        }
        controlsView.findViewById<ImageView>(R.id.btnZoomIn).setOnClickListener {
            mapfitMap.setZoom(mapfitMap.getZoom() + ZOOM_STEP_LEVEL,
                    ANIMATION_DURATION)
        }
        controlsView.findViewById<ImageView>(R.id.btnZoomOut).setOnClickListener {
            mapfitMap.setZoom(mapfitMap.getZoom() - ZOOM_STEP_LEVEL,
                    ANIMATION_DURATION)
        }
    }


    fun getMapAsync(onMapReadyCallback: OnMapReadyCallback) {

        tangramMap = tangramMapView.getMap { sceneId, sceneError ->
            onMapReady()
            onMapReadyCallback.onMapReady(mapfitMap)
        }

        mapOptions = MapOptions(this, tangramMap)

        tangramMap.loadSceneFile(mapOptions.mapTheme.toString())
    }

    private fun onMapReady() {
        tangramMap.setDoubleTapResponder(zoomOnDoubleTap)

        tangramMap.setTapResponder(object : TouchInput.TapResponder {
            override fun onSingleTapUp(x: Float, y: Float): Boolean {
                return true
            }

            override fun onSingleTapConfirmed(x: Float, y: Float): Boolean {
                async(UI) {
                    tangramMap.pickMarker(x, y)
                }
                return true
            }
        })

        tangramMap.setMarkerPickListener(markerPickListener())

        tangramMap.setDoubleTapResponder(zoomOnDoubleTap)


    }

    private fun markerPickListener(): (MarkerPickResult?, Float, Float) -> Unit {
        return { markerPickResult, _, _ ->

            markerPickResult?.run {
                val annotation = annotationLayer.getAnnotations().find {
                    it.getId() == markerPickResult.marker.markerId
                }

                annotation?.let {
                    when (it) {
                        is Marker -> markerClickListener?.onMarkerClicked(it)
                        is Polyline -> {
                        }
                        else -> {
                        }
                    }

                }
            }
        }
    }

    private val zoomOnDoubleTap = { x: Float, y: Float ->
        val latLng = tangramMap.screenPositionToLngLat(PointF(x, y))
        tangramMap.setPositionEased(latLng, ANIMATION_DURATION)
        tangramMap.setZoomEased(tangramMap.zoom + 1f, ANIMATION_DURATION)
        true
    }

    private val mapfitMap = object : MapfitMap() {

        override fun setCenterWithLayer(layer: Layer, duration: Long, paddingPercentage: Float) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setBounds(latLngBounds: LatLngBounds) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getBounds(): LatLngBounds {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setOnMapClickListener(onMapClickListener: OnMapClickListener) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setOnMapDoubleClickListener(onMapDoubleClickListener: OnMapDoubleClickListener) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

        override fun setOnMarkerClickListener(markerClickListener: OnMarkerClickListener) {
            this@MapView.markerClickListener = markerClickListener
        }

        override fun addMarker(latLng: LatLng): Marker {
            val tMarker = tangramMap.addMarker()

            tMarker.setPointEased(LngLat(latLng.lon, latLng.lat), 200, MapController.EaseType.CUBIC)

            val mapfitMarker = Marker(tMarker)

            mapfitMarker.setPosition(latLng) // no access to position of Tangram Marker
            annotationLayer.add(mapfitMarker)

            return mapfitMarker
        }

        override fun addPolygon(polygon: List<List<LatLng>>): Polygon {


            val poly = polygon.map {
                it.map {
                    LngLat(it.lon, it.lat)
                }
            }


            val tMarker = tangramMap.addMarker()
            tMarker.setPolygon(com.mapzen.tangram.geometry.Polygon(poly, null))


            val mapfitPolygon = Polygon(tMarker)

            return mapfitPolygon
        }

        override fun addPolyline(): Polyline {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getZoom(): Float {
            return tangramMap.zoom
        }

        override fun setCenter(latLng: LatLng, duration: Long) {
            tangramMap.setPositionEased(
                    LngLat(latLng.lon, latLng.lat),
                    duration.toInt(),
                    MapController.EaseType.CUBIC
            )
        }

        override fun getCenter(): LatLng {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun reCenter(duration: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addLayer(layer: Layer) {
        }

        override fun removeLayer(layer: Layer) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun removeMarker(marker: Marker): Boolean =
                tangramMap.removeMarker(marker.getMarker())


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
                    !in 1F..mapOptions.maxZoom -> Log.w("MapView", "Zoom level exceeds maximum level.")
                    else -> tangramMap.setZoomEased(zoomLevel, duration.toInt())
                }
            }
        }
    }

    fun setZoomControlVisibility(visible: Boolean) {
        zoomControlsView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun getDirectionView(): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getUiControlView(): View =
            LayoutInflater.from(context)
                    .inflate(R.layout.overlay_map_controls, this, false)
}