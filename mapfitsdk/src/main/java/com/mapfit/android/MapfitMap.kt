package com.mapfit.android

import android.support.annotation.FloatRange
import android.view.View
import com.mapfit.android.annotations.Annotation
import com.mapfit.android.annotations.Marker
import com.mapfit.android.annotations.Polygon
import com.mapfit.android.annotations.Polyline
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback
import com.mapfit.android.annotations.callback.OnMarkerClickListener
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.LatLngBounds
import org.jetbrains.annotations.TestOnly


/**
 * Controller for the map.
 *
 * Created by dogangulcan on 12/19/17.
 */
abstract class MapfitMap {

    /**
     * Sets the map center to the input coordinate values.
     *
     * @param latLng coordinates
     */
    abstract fun setCenter(latLng: LatLng)

    /**
     * Sets the map center to the input coordinate values and the duration of the entering animation
     *
     * @param latLng coordinates
     * @param duration for centering animation
     */
    abstract fun setCenter(latLng: LatLng, duration: Long = 0)

    /**
     * Sets the map center to the input layer's coordinate values.
     *
     * @param layer the map will center accordingly to
     */
    protected abstract fun setCenterWithLayer(layer: Layer)

    /**
     * Add the specified input layer to the map. All annotations in the layer will be added to the map.
     *
     * @param layer to add to the map
     */
    abstract fun addLayer(layer: Layer)


    /**
     * Returns the list of layers bound to the map view.
     *
     * @return layers on the map as list
     */
    abstract fun getLayers(): List<Layer>

    /**
     * Returns the center coordinates of the map.
     *
     * @return [LatLng]
     */
    abstract fun getCenter(): LatLng

    /**
     * Adds the marker to the map at the input coordinate position. Returns marker for styling and
     * place info customization.
     *
     * @return marker that is added to the given coordinates
     */
    abstract fun addMarker(latLng: LatLng): Marker

    /**
     * Adds the marker to the map at the coordinate position returned by geocoding the input
     * address. Returns marker for styling and place info customization.
     *
     * @param address an address such as "119w 24th st NY" venue names are shouldn't be given
     * @param withBuilding flag for building polygon
     * @param onMarkerAddedCallback for response and errors
     */
    abstract fun addMarker(
        address: String,
        withBuilding: Boolean = true,
        onMarkerAddedCallback: OnMarkerAddedCallback
    )

    /**
     * Removes the marker from the map.
     *
     * @param marker to be removed
     * @return isRemoved
     */
    abstract fun removeMarker(marker: Marker)

    /**
     * Adds the polyline to the map. Returns polyline for styling.
     *
     * @return polyline
     */
    abstract fun addPolyline(line: List<LatLng>): Polyline

    /**
     * Removes given [Polyline] from the [MapView].
     *
     * @param polyline to be removed
     */
    abstract fun removePolyline(polyline: Polyline)

    /**
     * Removes the polyline from the map.
     *
     * @return polygon
     */
    abstract fun addPolygon(polygon: List<List<LatLng>>): Polygon

    /**
     * Removes the polygon from the map.
     *
     * @param polygon to be removed
     */
    abstract fun removePolygon(polygon: Polygon)

    /**
     * Removes the specified input layer from the map.
     *
     * @param layer to be removed
     */
    abstract fun removeLayer(layer: Layer)

    /**
     * Sets the current zoom level of the map.
     *
     * @param zoomLevel Zoom level for the view
     */
    abstract fun setZoom(zoomLevel: Float)

    /**
     * Sets the current zoom level of the map and the duration of the zoom animation.
     *
     * @param zoomLevel Zoom level for the view
     * @param duration optional duration for zooming in milliseconds
     */
    abstract fun setZoom(zoomLevel: Float, duration: Int = 0)

    /**
     * Returns the current zoom level of the map.
     *
     * @return current zoom level of the map
     */
    abstract fun getZoom(): Float

    /**
     * Sets the map bounds using the [LatLngBounds] - the southwest and northeast corners of the
     * bounding box.
     *
     * @param bounds
     * @param padding between map and bounds as percentage. For 10% padding, you can pass 0.1f.
     */
    abstract fun setLatLngBounds(
        bounds: LatLngBounds, @FloatRange(
            from = 0.0,
            to = 1.0
        ) padding: Float
    )

    /**
     * Returns the bounding coordinates for the map.
     *
     * @return latLngBounds
     */
    abstract fun getLatLngBounds(): LatLngBounds

    /**
     * Sets [OnMapClickListener] for [MapView] that single click events will be passed to.
     */
    abstract fun setOnMapClickListener(listener: OnMapClickListener)

    /**
     * Sets [OnMapDoubleClickListener] for [MapView] that double click events will be passed to.
     */
    abstract fun setOnMapDoubleClickListener(listener: OnMapDoubleClickListener)

    /**
     * Sets [OnMapLongClickListener] for [MapView] that long click events will be passed to.
     */
    abstract fun setOnMapLongClickListener(listener: OnMapLongClickListener)

    /**
     * Sets [OnMarkerClickListener] for [MapView] that marker click events will be passed to.
     */
    abstract fun setOnMarkerClickListener(listener: OnMarkerClickListener)

    /**
     * Sets [OnMapPanListener] for [MapView] that pan events will be passed to.x
     */
    abstract fun setOnMapPanListener(listener: OnMapPanListener)

    /**
     * Sets [OnMapPinchListener] for [MapView] that pan events will be passed to.x

     * @param listener pinch events will be passed to
     */
    abstract fun setOnMapPinchListener(listener: OnMapPinchListener)

    /**
     * Enables customization of the place info view, which is displayed when a marker is tapped.
     *
     * @param adapter the callback to be invoked to obtain your custom place info.
     */
    abstract fun setPlaceInfoAdapter(adapter: PlaceInfoAdapter)

    /**
     * Sets a callback that's invoked when the user clicks on an info window.
     *
     * @param listener The callback that's invoked when the user clicks on an info window.
     *                 To unset the callback, use null.
     */
    abstract fun setOnPlaceInfoClickListener(listener: OnPlaceInfoClickListener)

    /**
     * MapOptions can be used to changing options for the map. For instance, setting maximum zoom
     * level or turning zoom controls off.
     */
    abstract fun getMapOptions(): MapOptions

    /**
     * Returns [DirectionsOptions] to interact with DirectionsAPI to drawing directions.
     *
     * @return [DirectionsOptions]
     */
    abstract fun getDirectionsOptions(): DirectionsOptions


    /**
     * Returns the current rotation of the map.
     *
     *@return counter-clockwise rotation in radians. North is 0
     */
    abstract fun getRotation(): Float

    /**
     * Sets the current rotation level of the map.
     *
     * @param rotation in radians
     */
    abstract fun setRotation(rotation: Float)

    /**
     * Sets the current rotation level of the map and the duration of the rotation animation.
     *
     * @param rotation in radians
     * @param duration duration of the rotation in milliseconds
     */
    abstract fun setRotation(rotation: Float, duration: Int = 0)


    /**
     * Sets the current tilt level of the map.
     *
     * @param angle in radians; 0 is straight down
     */
    abstract fun setTilt(angle: Float)


    /**
     * Sets the current tilt level of the map and the duration of the tilt animation.
     *
     * @param angle in radians, 0 is straight down
     * @param duration duration of the tilting in milliseconds
     */
    abstract fun setTilt(angle: Float, duration: Long)

    /**
     * Returns the current tilt level of the map.
     *
     * @return tilt angle in radians, 0 is to straight down
     */
    abstract fun getTilt(): Float

    /**
     * Map will be re-centered to the last position it is centered.
     */
    abstract fun reCenter()

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

    @TestOnly
    internal abstract fun has(annotation: Annotation): Boolean

}