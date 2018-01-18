package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.annotations.Marker
import com.mapfit.mapfitsdk.annotations.Polygon
import com.mapfit.mapfitsdk.annotations.Polyline
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerClickListener
import com.mapfit.mapfitsdk.annotations.callback.OnPolygonClickListener
import com.mapfit.mapfitsdk.annotations.callback.OnPolylineClickListener
import com.mapfit.mapfitsdk.geometry.LatLng
import com.mapfit.mapfitsdk.geometry.LatLngBounds


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
     * @param duration optional, the camera will move with ease.
     */
    abstract fun setCenter(latLng: LatLng)

    abstract fun setCenter(latLng: LatLng, duration: Long = 0)

    /**
     * Sets the center of the map accordingly to the layer.
     *
     * @param layer the map will center accordingly to
     */
    protected abstract fun setCenterWithLayer(layer: Layer, duration: Long = 0, paddingPercentage: Float)

    /**
     * Adds the given layer to the map.
     *
     * @param layer to add to the map
     */
    abstract fun addLayer(layer: Layer)

    abstract fun getLayers(): List<Layer>

    /**
     * @return center of the visible map
     */
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
    protected abstract fun addPolyline(): Polyline

    protected abstract fun removePolyline(polyline: Polyline)

    /**
     * Adds a polygon to default layer.
     *
     * @return polygon
     */
    protected abstract fun addPolygon(polygon: List<List<LatLng>>): Polygon

    protected abstract fun removePolygon(polygon: Polygon)

    abstract fun removeLayer(layer: Layer)

    /**
     * Sets zoom level of the map.
     *
     * @param zoomLevel Zoom level for the view
     * @param duration optional duration for zooming in milliseconds
     */
    abstract fun setZoom(zoomLevel: Float, duration: Int = 0)

    /**
     * @return current zoom level of the map
     */
    abstract fun getZoom(): Float

    protected abstract fun setBounds(latLngBounds: LatLngBounds)

    protected abstract fun getBounds(): LatLngBounds

    abstract fun setOnMapClickListener(onMapClickListener: OnMapClickListener)

    abstract fun setOnMapDoubleClickListener(onMapDoubleClickListener: OnMapDoubleClickListener)

    protected abstract fun setOnMarkerClickListener(onMarkerClickListener: OnMarkerClickListener)

    protected abstract fun setOnPolylineClickListener(onPolylineClickListener: OnPolylineClickListener)

    protected abstract fun setOnPolygonClickListener(onPolygonClickListener: OnPolygonClickListener)

    /**
     * MapOptions can be used to changing options for the map. For instance, setting maximum zoom
     * level or turning zoom controls off.
     */
    protected abstract fun getMapOptions(): MapOptions

    protected abstract fun getDirectionsOptions(): DirectionsOptions

    protected abstract fun setTilt(angle: Float)

    protected abstract fun getTilt(): Float

    protected abstract fun setRotation(angle: Float)

    protected abstract fun getRotation(): Float

    /**
     * Will reCenter the map.
     * @param duration if given, the camera will move with ease.
     */
    protected abstract fun reCenter(duration: Long = 0)

}