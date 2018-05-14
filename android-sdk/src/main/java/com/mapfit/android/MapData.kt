package com.mapfit.android

import com.mapfit.android.annotations.Marker
import com.mapfit.android.annotations.Polygon
import com.mapfit.android.annotations.Polyline

/**
 * `MapData` is a named collection of drawable map features.
 */
class MapData
/**
 * For package-internal use only; create a new `MapData`
 *
 * @param name    The name of the associated data source
 * @param pointer The markerId to the native data source, encoded as a long
 * @param map     The `MapController` associated with this data source
 */
internal constructor(var name: String, var id: Long, private var map: MapController?) {

    /**
     * Remove this `MapData` from the map it is currently associated with. Using this object
     * after `remove` is called will cause an exception to be thrown. `remove` is called
     * on every `MapData` associated with a map when its `MapController` is destroyed.
     */
    fun remove() {
        map!!.removeDataLayer(this)
        id = 0
        map = null
    }

    /**
     * Add a point feature to this collection.
     * the scene file used by the map; may be null.
     *
     * @return This object, for chaining.
     */
    fun addPoint(marker: Marker): MapData {
        val coordinates = DoubleArray(2)
        coordinates[0] = marker.position.lng
        coordinates[1] = marker.position.lat

        map!!.addFeature(
            id,
            coordinates,
            null,
            null
        )

        return this
    }

    fun addPolyline(polyline: Polyline): MapData {
        map?.let {
            it.addFeature(
                id,
                polyline.coordinates,
                null,
                polyline.getProperties(polyline.getIdForMap(it).toString())
            )
        }
        return this
    }

    /**
     * Add a polygon feature to this collection.
     *
     * @param polygon A list of rings describing the shape of the feature. Each
     * ring is a list of coordinates in which the first point is the same as the last point. The
     * first ring is taken as the "exterior" of the polygon and rings with opposite winding are
     * considered "holes".
     * @return This object, for chaining.
     */
    fun addPolygon(polygon: Polygon): MapData {
        map?.let {
            it.addFeature(
                id,
                polygon.coordinates,
                polygon.rings,
                polygon.getProperties(polygon.getIdForMap(it).toString())
            )
        }
        return this
    }

    /**
     * Add features described in a GeoJSON string to this collection.
     *
     * @param data A string containing a [GeoJSON](http://geojson.org/) FeatureCollection
     * @return This object, for chaining.
     */
    fun addGeoJson(data: String): MapData {
        map!!.addGeoJson(id, data)
        return this
    }


    /**
     * Remove all features from this collection.
     *
     * @return This object, for chaining.
     */
    fun clear(): MapData {
        map!!.clearTileSource(id)
        return this
    }


}
