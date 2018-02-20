package com.mapfit.tetragon;

import android.support.annotation.Keep;

import com.mapfit.mapfitsdk.geometry.LatLng;

/**
 * {@code MarkerPickResult} represents labels that can be selected on the screen
 */
@Keep
public class MarkerPickResult {

    private com.mapfit.mapfitsdk.annotations.Marker marker;
    private LatLng coordinates;

    private MarkerPickResult(com.mapfit.mapfitsdk.annotations.Marker marker, double longitude, double latitude) {
        this.marker = marker;
        this.coordinates = new LatLng(longitude, latitude);
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
        return coordinates;
    }

}
