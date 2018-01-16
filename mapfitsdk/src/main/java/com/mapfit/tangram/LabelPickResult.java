package com.mapfit.tangram;

import android.support.annotation.Keep;

import com.mapfit.mapfitsdk.geometry.LatLng;

import java.util.Map;

/**
 * {@code LabelPickResult} represents labels that can be selected on the screen
 */
@Keep
public class LabelPickResult {

    /**
     * Options for the type of LabelPickResult
     */
    public enum LabelType {
        ICON,
        TEXT,
    }

    private LatLng coordinates;
    private LabelType type;
    private Map<String, String> properties;

    private LabelPickResult(double longitude, double latitude, int type, Map<String, String> properties) {
        this.properties = properties;
        this.coordinates = new LatLng(longitude, latitude);
        this.type = LabelType.values()[type];
    }

    public LabelType getType() {
        return this.type;
    }

    /**
     * @return The coordinate of the feature for which this label has been created
     */
    public LatLng getCoordinates() {
        return this.coordinates;
    }

    /**
     * @return A mapping of string keys to string or number values
     */
    public Map<String, String> getProperties() {
        return this.properties;
    }
}
