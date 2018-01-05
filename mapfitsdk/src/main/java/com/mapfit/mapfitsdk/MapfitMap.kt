package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.annotations.Marker
import com.mapfit.mapfitsdk.annotations.Polygon
import com.mapfit.mapfitsdk.annotations.Polyline
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
    internal abstract fun addMarker(latLng: LatLng): Marker

    internal abstract fun removeMarker(marker: Marker): Boolean

    /**
     * Adds a polyline to default layer.
     *
     * @return polyline
     */
    internal abstract fun addPolyline(): Polyline

    internal abstract fun removePolyline(polyline: Polyline)

    /**
     * Adds a polygon to default layer.
     *
     * @return polygon
     */
    internal abstract fun addPolygon(polygon: List<List<LatLng>>): Polygon

    internal abstract fun removePolygon(polygon: Polygon)

    abstract fun addLayer(layer: Layer)

    internal abstract fun removeLayer(layer: Layer)

    /**
     * Sets zoom level of the map.
     *
     * @param zoomLevel Zoom level for the view
     * @param duration optional duration for zooming in milliseconds
     */
    abstract fun setZoom(zoomLevel: Float, duration: Int = 0)

    abstract fun getZoom(): Float

    internal abstract fun setBounds(latLngBounds: LatLngBounds)

    internal abstract fun getBounds(): LatLngBounds

    abstract fun setOnMapClickListener(onMapClickListener: OnMapClickListener)

    abstract fun setOnMapDoubleClickListener(onMapDoubleClickListener: OnMapDoubleClickListener)

    internal abstract fun setOnMarkerClickListener(onMarkerClickListener: OnMarkerClickListener)

    internal abstract fun setOnPolylineClickListener(onPolylineClickListener: OnPolylineClickListener)

    internal abstract fun setOnPolygonClickListener(onPolygonClickListener: OnPolygonClickListener)

    internal abstract fun getMapOptions(): MapOptions

    internal abstract fun getDirectionsOptions(): DirectionsOptions

    internal abstract fun setTilt(angle: Float)

    internal abstract fun getTilt(): Float

    internal abstract fun setRotation(angle: Float)

    internal abstract fun getRotation(): Float

    /**
     * Will reCenter the map.
     * @param duration if given, the camera will move with ease.
     */
    internal abstract fun reCenter(duration: Long = 0)

}