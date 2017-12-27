package com.mapfit.mapfitsdk

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import com.mapfit.mapfitsdk.annotations.Annotation
import com.mapfit.mapfitsdk.annotations.AnnotationClickListener
import com.mapfit.mapfitsdk.annotations.Layer
import com.mapfit.mapfitsdk.annotations.Marker
import com.mapfit.mapfitsdk.geo.LatLng
import com.mapfit.mapfitsdk.utils.isValidZoomLevel
import com.mapzen.tangram.*
import com.mapzen.tangram.MapView
import com.mapzen.tangram.geometry.Polygon
import com.mapzen.tangram.geometry.Polyline
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

/**
 * The view map is drawn onto.
 *
 * Created by dogangulcan on 12/18/17.
 */
class MapView(
        context: Context,
        attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private lateinit var tangramMap: com.mapzen.tangram.MapController
    private lateinit var mapOptions: MapOptions
    private val tangramMapView by lazy { MapView(context, attributeSet) }
    private val annotationLayer = Layer("annotations")

    private val layers = mutableListOf(annotationLayer)

    private var annotationClickListener: AnnotationClickListener? = null

    init {
        addView(tangramMapView)
    }

    fun getMapAsync(onMapReadyCallback: OnMapReadyCallback) {

        tangramMap = tangramMapView.getMap { sceneId, sceneError ->
            onMapReady()
            onMapReadyCallback.onMapReady(mapfitMap)
        }

        mapOptions = MapOptions(tangramMap)

        tangramMap.loadSceneFile(mapOptions.scenePath.toString())

    }


    private fun onMapReady() {
        tangramMap.setDoubleTapResponder(zoomOnDoubleTap)

        annotationLayer.bindToMap(this)

        tangramMap.setTapResponder(object : TouchInput.TapResponder {
            override fun onSingleTapUp(x: Float, y: Float): Boolean {
                return true
            }

            override fun onSingleTapConfirmed(x: Float, y: Float): Boolean {
                async(UI) {

                    tangramMap.pickMarker(x, y)

//                    val marker = findMarkerAt(x, y)
//                    marker.await()?.let { annotationClickListener?.onAnnotationClick(it) }
                }
                return true
            }
        })


        tangramMap.setMarkerPickListener { markerPickResult, positionX, positionY ->
            markerPickResult?.run {
                val marker = annotationLayer.getAnnotations().find {
                    Log.i("MARKER CLICK ","$")
                    it.getId() == markerPickResult.marker.markerId
                }

                marker?.let { annotationClickListener?.onAnnotationClick(it) }
            }
        }


    }

    private fun findMarkerAt(x: Float, y: Float): Deferred<Annotation?> =
            bg {

                annotationLayer.getAnnotations().find {
                    val (x, y) = it.getLocation()

                    tangramMap.lngLatToScreenPosition(LngLat(y, x))
                    0 == 0
                }
            }

    private val zoomOnDoubleTap = { x: Float, y: Float ->
        val latLng = tangramMap.screenPositionToLngLat(PointF(x, y))
        tangramMap.setPositionEased(latLng, 200)
        tangramMap.setZoomEased(tangramMap.zoom + 1f, 200)
        true
    }

    private val mapfitMap = object : MapfitMap() {
        override fun setOnAnnotationClickListener(annotationClickListener: AnnotationClickListener) {
            this@MapView.annotationClickListener = annotationClickListener
        }

        override fun addBuilding(address: String) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addBuilding(latLng: LatLng) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addMarker(latLng: LatLng): Marker {
            val tMarker = tangramMap.addMarker()

            tMarker.setPointEased(LngLat(latLng.lon, latLng.lat), 200, MapController.EaseType.QUINT)
            Log.e("MARKER ADDED TO", " $ { latLng.lat latLng . lon }")
//            tMarker.setDrawOrder(7989100) // doesnt work

            val mapfitMarker = Marker(tMarker)
            annotationLayer.add(mapfitMarker)

            return mapfitMarker
        }

        override fun addPolygon(): Polygon {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addPolyline(): Polyline {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getZoomLevel(): Float {
            return tangramMap.zoom
        }

        override fun addMarkers(jsonString: String): MutableList<Marker> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setCenter(latLng: LatLng, duration: Long) {
            tangramMap.setPositionEased(LngLat(latLng.lon, latLng.lat), duration.toInt())
        }

        override fun getCenter(): LatLng {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun recenter(duration: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addLayer(midtownLayer: Layer) {
            midtownLayer.bindToMap(this@MapView)
        }

        override fun removeLayer(layer: Layer) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun removeMarker(marker: Marker) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun removePolygon(polygon: Polygon) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun removePolyline(polyline: Polyline) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setMapOptions(mapOptions: MapOptions) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getMapOptions(): MapOptions {
            return mapOptions
        }

        override fun setZoomLevel(zoomLevel: Float, duration: Long) {
            if (isValidZoomLevel(zoomLevel)) {
                when (zoomLevel) {
                    !in 1F..mapOptions.maxZoom -> Log.w("MapView", "Zoom level exceeds maximum level.")
                    else -> tangramMap.setZoomEased(zoomLevel, duration.toInt())
                }
            }
        }
    }

}