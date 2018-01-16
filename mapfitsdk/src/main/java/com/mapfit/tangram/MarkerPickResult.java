package com.mapfit.tangram;

import android.support.annotation.Keep;

import com.mapfit.mapfitsdk.geo.LatLng;

/**
 * {@code MarkerPickResult} represents labels that can be selected on the screen
 */
@Keep
public class MarkerPickResult {

    private com.mapfit.mapfitsdk.annotations.Marker marker;
    private LngLat coordinates;

    private MarkerPickResult(com.mapfit.mapfitsdk.annotations.Marker marker, double longitude, double latitude) {
        this.marker = marker;
        this.coordinates = new LngLat(longitude, latitude);
    }

    /**
     * @return The marker associated with the selection
     */
    public com.mapfit.mapfitsdk.annotations.Marker getMarker() {
        return this.marker;
    }

    /**
     * @return The coordinate of the feature for which this label has been created
     */
    public LatLng getCoordinates() {
        return new LatLng(coordinates.latitude, coordinates.longitude);
    }

}
