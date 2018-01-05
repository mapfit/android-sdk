package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.annotations.*
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerClickListener
import com.mapfit.mapfitsdk.annotations.callback.OnPolygonClickListener
import com.mapfit.mapfitsdk.annotations.callback.OnPolylineClickListener
import com.mapfit.mapfitsdk.geo.LatLng
import com.mapfit.mapfitsdk.geo.LatLngBounds


/**
 * Controller for the map.
 *
 * Created by dogangulcan on 12/19/17.
 */
abstract class MapfitMap {

    /**
     * Sets the center of the map.
     *
     * @param latLng coordinates
     * @param duration if given, the camera will move with ease.
     */
    abstract fun setCenter(latLng: LatLng, duration: Long = 0)

    /**
     * Sets the center of the map accordingly to the layer.
     *
     * @param layer the map will center accordingly to
     */
    abstract fun setCenterWithLayer(layer: Layer, duration: Long = 0, paddingPercentage: Float)

    abstract fun getCenter(): LatLng

    /**
     * Adds a marker on the default layer.
     *
     * @return marker
     */
    abstract fun addMarker(latLng: LatLng): Marker

    abstract fun removeMarker(marker: Marker): Boolean

    /**
     * Adds a polyline to default layer.
     *
     * @return polyline
     */
    abstract fun addPolyline(): Polyline

    abstract fun removePolyline(polyline: Polyline)

    /**
     * Adds a polygon to default layer.
     *
     * @return polygon
     */
    abstract fun addPolygon(polygon: List<List<LatLng>>): Polygon

    abstract fun removePolygon(polygon: Polygon)

    abstract fun addLayer(layer: Layer)

    abstract fun removeLayer(layer: Layer)


    abstract fun setZoom(zoomLevel: Float, duration: Int = 0)

    abstract fun getZoom(): Float

    abstract fun setBounds(latLngBounds: LatLngBounds)

    abstract fun getBounds(): LatLngBounds

    abstract fun setOnMapClickListener(onMapClickListener: OnMapClickListener)

    abstract fun setOnMapDoubleClickListener(onMapDoubleClickListener: OnMapDoubleClickListener)

    abstract fun setOnMarkerClickListener(onMarkerClickListener: OnMarkerClickListener)

    abstract fun setOnPolylineClickListener(onPolylineClickListener: OnPolylineClickListener)

    abstract fun setOnPolygonClickListener(onPolygonClickListener: OnPolygonClickListener)

    abstract fun getMapOptions(): MapOptions

    abstract fun getDirectionsOptions(): DirectionsOptions

    abstract fun setTilt(angle: Float)

    abstract fun getTilt(): Float

    abstract fun setRotation(angle: Float)

    abstract fun getRotation(): Float

    /**
     * Will reCenter the map.
     * @param duration if given, the camera will move with ease.
     */
    abstract fun reCenter(duration: Long = 0)

}