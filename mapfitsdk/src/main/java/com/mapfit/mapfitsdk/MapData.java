package com.mapfit.mapfitsdk;

import com.mapfit.mapfitsdk.annotations.Marker;
import com.mapfit.mapfitsdk.annotations.Polyline;
import com.mapfit.mapfitsdk.geometry.LatLng;
import com.mapfit.tangram.geometry.Geometry;

import java.util.List;
import java.util.Map;

/**
 * {@code MapData} is a named collection of drawable map features.
 */
public class MapData {

    String name;
    long id = 0;
    MapController map;

    /**
     * For package-internal use only; create a new {@code MapData}
     *
     * @param name    The name of the associated data source
     * @param pointer The markerId to the native data source, encoded as a long
     * @param map     The {@code MapController} associated with this data source
     */
    MapData(String name, long pointer, MapController map) {
        this.name = name;
        this.id = pointer;
        this.map = map;
    }

    /**
     * Add a geometry feature to this data collection
     *
     * @param geometry The feature to add
     */
    protected void addFeature(Geometry geometry) {
        map.addFeature(id,
                geometry.getCoordinateArray(),
                geometry.getRingArray(),
                geometry.getPropertyArray());
    }

    /**
     * Get the name of this {@code MapData}.
     *
     * @return The name.
     */
    public String name() {
        return name;
    }

    /**
     * Remove this {@code MapData} from the map it is currently associated with. Using this object
     * after {@code remove} is called will cause an exception to be thrown. {@code remove} is called
     * on every {@code MapData} associated with a map when its {@code MapController} is destroyed.
     */
    public void remove() {
        map.removeDataLayer(this);
        id = 0;
        map = null;
    }

    /**
     * Add a point feature to this collection.
     *
     * @param point      The coordinates of the feature.
     * @param properties The properties of the feature, used for filtering and styling according to
     *                   the scene file used by the map; may be null.
     * @return This object, for chaining.
     */
    public MapData addPoint(Marker marker) {
        double[] coordinates = new double[2];
        coordinates[0] = marker.getPosition().getLon();
        coordinates[1] = marker.getPosition().getLat();

        map.addFeature(id,
                coordinates,
                null,
                marker.getMarkerOptions().getProperties());
        return this;
    }


    public MapData addPolyline(Polyline polyline) {
        map.addFeature(id,
                polyline.getCoordinates(),
                null,
                polyline.getPolylineOptions().getProperties());

        return this;
    }

    /**
     * Add a polygon feature to this collection.
     *
     * @param polygon    A list of rings describing the shape of the feature. Each
     *                   ring is a list of coordinates in which the first point is the same as the last point. The
     *                   first ring is taken as the "exterior" of the polygon and rings with opposite winding are
     *                   considered "holes".
     * @param properties The properties of the feature, used for filtering and styling according to
     *                   the scene file used by the map; may be null.
     * @return This object, for chaining.
     */
    public MapData addPolygon(List<List<LatLng>> polygon, Map<String, String> properties) {
//        addFeature(new Polygon(polygon, properties));
        return this;
    }

    /**
     * Add features described in a GeoJSON string to this collection.
     *
     * @param data A string containing a <a href="http://geojson.org/">GeoJSON</a> FeatureCollection
     * @return This object, for chaining.
     */
    public MapData addGeoJson(String data) {
        map.addGeoJson(id, data);
        return this;
    }

    /**
     * Remove all features from this collection.
     *
     * @return This object, for chaining.
     */
    public MapData clear() {
        map.clearTileSource(id);
        return this;
    }

}
