package com.mapfit.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mapfit.android.MapView;
import com.mapfit.android.MapfitMap;
import com.mapfit.android.OnMapClickListener;
import com.mapfit.android.OnMapDoubleClickListener;
import com.mapfit.android.OnMapReadyCallback;
import com.mapfit.android.annotations.MapfitMarker;
import com.mapfit.android.annotations.Marker;
import com.mapfit.android.annotations.MarkerOptions;
import com.mapfit.android.annotations.Polyline;
import com.mapfit.android.annotations.PolylineOptions;
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback;
import com.mapfit.android.directions.Directions;
import com.mapfit.android.directions.DirectionsCallback;
import com.mapfit.android.directions.model.Leg;
import com.mapfit.android.directions.model.Route;
import com.mapfit.android.geometry.LatLng;
import com.mapfit.android.utils.PolyUtils;
import com.mapfit.mapfitdemo.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JavaActivity extends AppCompatActivity {

    MapView mapView;
    MapfitMap mapfitMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NotNull MapfitMap mapfitMap) {
                setupMap(mapfitMap);
            }
        });

    }

    private void setupMap(MapfitMap mapfitMap) {
        this.mapfitMap = mapfitMap;

        mapfitMap.setCenter(new LatLng(40.74405, -73.99324));
        mapfitMap.setZoom(14f);

        placeMarker();
        placeMarkerWithAddress();
        getDirections();

        setEventListeners();

        // enable ui controls
        mapfitMap.getMapOptions().setRecenterButtonVisible(true);
        mapfitMap.getMapOptions().setZoomControlVisible(true);
        mapfitMap.getMapOptions().setCompassButtonVisible(true);
    }

    private void setEventListeners() {
        mapfitMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClicked(@NotNull LatLng latLng) {

            }
        });

        mapfitMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener() {
            @Override
            public void onMapDoubleClicked(@NotNull LatLng latLng) {

            }
        });
    }

    private void getDirections() {
        String originAddress = "111 Macdougal Street new york ny";
        String destinationAddress = "119 W 24th Street new york ny";

        new Directions().route(
                originAddress,
                destinationAddress,
                new DirectionsCallback() {
                    @Override
                    public void onSuccess(@NotNull Route route) {
                        for (Leg leg : route.getTrip().getLegs()) {
                            List<LatLng> points = PolyUtils.decodePolyline(leg.getShape());
                            Polyline polyline = mapfitMap
                                    .addPolyline(new PolylineOptions()
                                            .points(points));
                        }
                    }

                    @Override
                    public void onError(@NotNull String message, @NotNull Exception e) {

                    }
                });
    }

    private void placeMarker() {
        LatLng position = new LatLng(40.744023, -73.993150);
        Marker marker = mapfitMap.addMarker(new MarkerOptions().position(position));
    }

    private void placeMarkerWithAddress() {
        String flatironBuildingAddress = "175 5th Ave, New York, NY 10010";

        MarkerOptions markerOptions = new MarkerOptions()
                .streetAddress(flatironBuildingAddress)
                .addBuildingPolygon(true)
                .icon(MapfitMarker.EDUCATION);

        mapfitMap.addMarker(
                markerOptions,
                new OnMarkerAddedCallback() {
                    @Override
                    public void onMarkerAdded(@NotNull Marker marker) {
                        // let's change marker's icon!
                        marker.setIcon(MapfitMarker.EDUCATION);
                    }

                    @Override
                    public void onError(@NotNull Exception exception) {

                    }
                });
    }

}
