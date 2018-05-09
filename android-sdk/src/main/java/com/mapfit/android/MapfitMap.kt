package com.mapfit.android

import android.graphics.PointF
import android.support.annotation.FloatRange
import android.support.annotation.RestrictTo
import android.view.View
import com.mapfit.android.annotations.*
import com.mapfit.android.annotations.Annotation
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback
import com.mapfit.android.annotations.callback.OnMarkerClickListener
import com.mapfit.android.annotations.callback.OnPolygonClickListener
import com.mapfit.android.annotations.callback.OnPolylineClickListener
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.LatLngBounds
import org.jetbrains.annotations.TestOnly


/**
 * Controller for the map.
 *
 * Created by dogangulcan on 12/19/17.
 */
class MapfitMap internal constructor(
    private val mapView: MapView,
    private val mapController: MapController
) {

    /**
     * Sets the map center to the input coordinate values and the duration of the entering animation
     *
     * @param latLng coordinates
     * @param duration for centering animation
     */
    @JvmOverloads
    fun setCenter(latLng: LatLng, duration: Long = 0) {
        when (duration) {
            0L -> mapController.position = latLng
            else -> mapController.setPositionEased(
                latLng,
                duration.toInt(),
                MapView.DEFAULT_EASE,
                true
            )
        }

        mapView.updatePlaceInfoPosition(true)
    }

    /**
     * Sets the map center to the input layer's coordinate values.
     *
     * @param layer the map will center accordingly to
     */
    fun setCenterWithLayer(layer: Layer) {
        mapController.position = layer.getLatLngBounds().center
        mapView.updatePlaceInfoPosition(true)
    }

    /**
     * Add the specified input layer to the map. All annotations in the layer will be added to the map.
     *
     * @param layer to add to the map
     */
    fun addLayer(layer: Layer) {
        if (!mapView.layers.contains(layer)) {
            mapView.layers.add(layer)
            layer.addMap(mapController)
        }
    }

    /**
     * Returns the list of layers bound to the map view.
     *
     * @return layers on the map as list
     */
    fun getLayers() = mapView.layers

    /**
     * Returns the center coordinates of the map.
     *
     * @return [LatLng]
     */
    fun getCenter(): LatLng? = mapController.position

    /**
     * Adds the marker to the map at the input coordinate position or address with Geocoding.
     *
     * @param markerOptions options and settings for the marker
     * @param onMarkerAddedCallback optional callback for adding marker
     * @return marker that is added or will be added to the given position or address
     */
    @JvmOverloads
    fun addMarker(
        markerOptions: MarkerOptions,
        onMarkerAddedCallback: OnMarkerAddedCallback? = null
    ): Marker {
        val marker = mapController.addMarker(markerOptions)

        if (markerOptions.geocode || markerOptions.buildingPolygon) {
            marker.geocode(onMarkerAddedCallback)
        }

        return marker
    }

    /**
     * Removes the marker from the map.
     *
     * @param marker to be removed
     * @return isRemoved
     */
    fun removeMarker(marker: Marker) = marker.remove(mapController)

    /**
     * Adds the polyline to the map. Returns polyline for styling.
     *
     * @return polyline
     */
    fun addPolyline(line: List<LatLng>): Polyline = mapController.addPolyline(line)

    /**
     * Removes given [Polyline] from the [MapView].
     *
     * @param polyline to be removed
     */
    fun removePolyline(polyline: Polyline) = polyline.remove(mapController)

    /**
     * Removes the polyline from the map.
     *
     * @return polygon
     */
    fun addPolygon(polygon: List<List<LatLng>>): Polygon = mapController.addPolygon(polygon)

    /**
     * Removes the polygon from the map.
     *
     * @param polygon to be removed
     */
    fun removePolygon(polygon: Polygon) = polygon.remove(mapController)

    /**
     * Removes the specified input layer from the map.
     *
     * @param layer to be removed
     */
    fun removeLayer(layer: Layer) {
        layer.annotations.forEach {
            it.mapBindings[mapController]?.let { id ->
                if (it is Marker && mapView.activePlaceInfo?.marker == it) {
                    mapView.activePlaceInfo?.dispose(true)
                }
                mapController.removeMarker(id)
                it.mapBindings.remove(mapController)
            }
        }

        mapView.layers.remove(layer)
    }

    /**
     * Sets the current zoom level of the map and the duration of the zoom animation.
     *
     * @param zoomLevel Zoom level for the view
     * @param duration optional duration for zooming in milliseconds
     */
    @JvmOverloads
    fun setZoom(
        zoomLevel: Float,
        duration: Long = 0
    ) {
        val normalizedZoomLevel = mapView.normalizeZoomLevel(zoomLevel)

        when (duration) {
            0L -> mapController.zoom = normalizedZoomLevel
            else -> mapController.setZoomEased(normalizedZoomLevel, duration.toInt())
        }
    }

    /**
     * Returns the current zoom level of the map.
     *
     * @return current zoom level of the map
     */
    fun getZoom() = mapController.zoom

    /**
     * Sets the map bounds using the [LatLngBounds] - the southwest and northeast corners of the
     * bounding box.
     *
     * @param bounds
     * @param padding between map and bounds as percentage. For 10% padding, you can pass 0.1f.
     */
    fun setLatLngBounds(
        bounds: LatLngBounds,
        @FloatRange(
            from = 0.0,
            to = 1.0
        )
        padding: Float
    ) {
        mapController.setLatLngBounds(bounds, padding)
        mapView.updatePlaceInfoPosition(true)
    }

    /**
     * Returns the bounding coordinates for the map.
     *
     * @return latLngBounds
     */
    fun getLatLngBounds(): LatLngBounds {
        val sw =
            mapController.screenPositionToLatLng(
                PointF(0f, mapView.viewHeight?.toFloat() ?: 0f)
            )
        val ne =
            mapController.screenPositionToLatLng(
                PointF(mapView.viewWidth?.toFloat() ?: 0f, 0f)
            )
        return LatLngBounds(ne ?: LatLng(), sw ?: LatLng())
    }

    /**
     * Sets [OnMapClickListener] for [MapView] that single click events will be passed to.
     */
    fun setOnMapClickListener(listener: OnMapClickListener) {
        mapView.mapClickListener = listener
    }

    /**
     * Sets [OnMapDoubleClickListener] for [MapView] that double click events will be passed to.
     */
    fun setOnMapDoubleClickListener(listener: OnMapDoubleClickListener) {
        mapView.mapDoubleClickListener = listener
    }

    /**
     * Sets [OnMapLongClickListener] for [MapView] that long click events will be passed to.
     */
    fun setOnMapLongClickListener(listener: OnMapLongClickListener) {
        mapView.mapLongClickListener = listener
    }

    /**
     * Sets [OnMarkerClickListener] for [MapView] that marker click events will be passed to.
     */
    fun setOnMarkerClickListener(listener: OnMarkerClickListener) {
        mapView.markerClickListener = listener
    }

    /**
     * Sets [OnPolylineClickListener] for [MapView] that polyline click events will be passed to.
     */
    fun setOnPolylineClickListener(listener: OnPolylineClickListener) {
        mapView.polylineClickListener = listener
    }

    /**
     * Sets [OnPolygonClickListener] for [MapView] that polygon click events will be passed to.
     */
    fun setOnPolygonClickListener(listener: OnPolygonClickListener) {
        mapView.polygonClickListener = listener
    }

    /**
     * Sets [OnMapPanListener] for [MapView] that pan events will be passed to.x
     */
    fun setOnMapPanListener(listener: OnMapPanListener) {
        mapView.mapPanListener = listener
    }

    /**
     * Sets [OnMapPinchListener] for [MapView] that pan events will be passed to.x

     * @param listener pinch events will be passed to
     */
    fun setOnMapPinchListener(listener: OnMapPinchListener) {
        mapView.mapPinchListener = listener
    }

    /**
     * Enables customization of the place info view, which is displayed when a marker is tapped.
     *
     * @param adapter the callback to be invoked to obtain your custom place info.
     */
    fun setPlaceInfoAdapter(adapter: PlaceInfoAdapter) {
        mapView.placeInfoAdapter = adapter
    }

    /**
     * Sets a callback that's invoked when the user clicks on an info window.
     *
     * @param listener The callback that's invoked when the user clicks on an info window.
     *                 To unset the callback, use null.
     */
    fun setOnPlaceInfoClickListener(listener: OnPlaceInfoClickListener) {
        mapView.onPlaceInfoClickListener = listener
    }

    /**
     * MapOptions can be used to changing options for the map. For instance, setting maximum zoom
     * level or turning zoom controls off.
     */
    fun getMapOptions() = mapView.mapOptions

    /**
     * Returns [DirectionsOptions] to interact with DirectionsAPI to drawing directions.
     *
     * @return [DirectionsOptions]
     */
    fun getDirectionsOptions() = mapView.directionsOptions


    /**
     * Returns the current rotation of the map.
     *
     *@return counter-clockwise rotation in radians. North is 0
     */
    fun getRotation() = mapController.rotation

    /**
     * Sets the current rotation level of the map and the duration of the rotation animation.
     *
     * @param rotation in radians
     * @param duration duration of the rotation in milliseconds
     */
    @JvmOverloads
    fun setRotation(
        rotation: Float,
        duration: Long = 0
    ) {
        when (duration) {
            0L -> mapController.rotation = rotation
            else -> mapController.setRotationEased(rotation, duration.toInt(), MapView.DEFAULT_EASE)
        }
    }

    /**
     * Sets the current tilt level of the map and the duration of the tilt animation.
     *
     * @param angle in radians, 0 is straight down
     * @param duration duration of the tilting in milliseconds
     */
    @JvmOverloads
    fun setTilt(
        angle: Float,
        duration: Long = 0
    ) {
        when (duration) {
            0L -> mapController.tilt = angle
            else -> mapController.setTiltEased(angle, duration, MapView.DEFAULT_EASE)
        }
    }

    /**
     * Returns the current tilt level of the map.
     *
     * @return tilt angle in radians, 0 is to straight down
     */
    fun getTilt() = mapController.tilt

    /**
     * Map will be re-centered to the last position it is centered.
     */
    fun reCenter() {
        mapController.reCenter()
        mapView.updatePlaceInfoPosition(true)
    }

    /**
     * Sets a callback that's invoked when a map theme is loaded or an error has occurred.
     *
     * @param listener invoked when the theme is loaded or an error has occurred.
     */
    fun setOnMapThemeLoadListener(listener: OnMapThemeLoadListener) {
        mapView.mapThemeLoadListener = listener
    }

    /**
     * Interface to be used to set custom view for Place Info.
     */
    interface PlaceInfoAdapter {

        /**
         * Called when a place info will be shown after a marker click.
         *
         * @param marker The marker the user clicked on.
         * @return View to be shown as a place info. If null is returned the default
         * info window will be shown.
         */
        fun getPlaceInfoView(marker: Marker): View
    }

    /**
     * Listener for capturing Place Info click events.
     */
    interface OnPlaceInfoClickListener {

        /**
         * Called when the user clicks on a place info.
         *
         * @param marker The marker of the place info that is clicked on.
         */
        fun onPlaceInfoClicked(marker: Marker)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @TestOnly
    internal fun has(annotation: Annotation) = mapController.contains(annotation)

}