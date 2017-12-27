package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.annotations.AnnotationClickListener
import com.mapfit.mapfitsdk.annotations.Layer
import com.mapfit.mapfitsdk.annotations.Marker
import com.mapfit.mapfitsdk.geo.LatLng
import com.mapzen.tangram.geometry.Polygon
import com.mapzen.tangram.geometry.Polyline


/**
 * Controller for the map.
 *
 * Created by dogangulcan on 12/19/17.
 */
abstract class MapfitMap {

    /**
     * Sets the center of the map.
     * @param latLng coordinates
     * @param duration if given, the camera will move with ease.
     */
    abstract fun setCenter(latLng: LatLng, duration: Long = 0)

    abstract fun getCenter(): LatLng

    /**
     * Will recenter the map.
     * @param duration if given, the camera will move with ease.
     */
    abstract fun recenter(duration: Long = 0)

    abstract fun addLayer(midtownLayer: Layer)

    abstract fun removeLayer(layer: Layer)

    /**
     * Adds a marker on the default layer.
     *
     * @return marker
     */
    abstract fun addMarker(latLng: LatLng = LatLng(0.0, 0.0)): Marker

    abstract fun removeMarker(marker: Marker)

    /**
     * Adds a polygon to default layer.
     *
     * @return polygon
     */
    abstract fun addPolygon(): Polygon

    abstract fun removePolygon(polygon: Polygon)

    /**
     * Adds a polyline to default layer.
     *
     * @return polyline
     */
    abstract fun addPolyline(): Polyline

    abstract fun removePolyline(polyline: Polyline)

    abstract fun setMapOptions(mapOptions: MapOptions)

    abstract fun getMapOptions(): MapOptions

    abstract fun setZoomLevel(zoomLevel: Float, duration: Long = 0)

    abstract fun getZoomLevel(): Float

    abstract fun addMarkers(jsonString: String): MutableList<Marker>

    abstract fun addBuilding(address: String)

    abstract fun addBuilding(latLng: LatLng)

    abstract fun setOnAnnotationClickListener(annotationClickListener : AnnotationClickListener)

}