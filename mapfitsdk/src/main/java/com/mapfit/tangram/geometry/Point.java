package com.mapfit.tangram.geometry;

import com.mapfit.mapfitsdk.geometry.LatLng;

import java.util.Map;

/**
 * {@code Point} is a single LngLat and its properties.
 */
public class Point extends Geometry {

    public Point(LatLng point, Map<String, String> properties) {
        this.coordinates = new double[2];
        coordinates[1] = point.getLon();
        coordinates[0] = point.getLat();
        if (properties != null) {
            this.properties = getStringMapAsArray(properties);
        }

    }
}
