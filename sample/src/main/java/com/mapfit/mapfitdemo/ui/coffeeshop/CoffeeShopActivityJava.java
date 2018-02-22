package com.mapfit.mapfitdemo.ui.coffeeshop;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mapfit.android.MapTheme;
import com.mapfit.android.annotations.callback.OnMarkerAddedCallback;
import com.mapfit.android.geometry.LatLngBounds;
import com.mapfit.mapfitdemo.R;
import com.mapfit.mapfitdemo.data.model.CoffeeShop;
import com.mapfit.mapfitdemo.module.coffeeshop.data.Repository;
import com.mapfit.mapfitdemo.ui.adapter.FilterAdapter;
import com.mapfit.mapfitdemo.ui.adapter.FilterType;
import com.mapfit.mapfitdemo.ui.adapter.OnFilterCheckedListener;
import com.mapfit.android.DirectionsOptions;
import com.mapfit.android.Layer;
import com.mapfit.android.MapView;
import com.mapfit.android.MapfitMap;
import com.mapfit.android.OnMapClickListener;
import com.mapfit.android.OnMapDoubleClickListener;
import com.mapfit.android.OnMapLongClickListener;
import com.mapfit.android.OnMapPanListener;
import com.mapfit.android.OnMapPinchListener;
import com.mapfit.android.OnMapReadyCallback;
import com.mapfit.android.annotations.Annotation;
import com.mapfit.android.annotations.Marker;
import com.mapfit.android.annotations.Polyline;
import com.mapfit.android.annotations.callback.OnMarkerClickListener;
import com.mapfit.android.directions.Directions;
import com.mapfit.android.directions.DirectionsCallback;
import com.mapfit.android.directions.DirectionsType;
import com.mapfit.android.directions.model.Route;
import com.mapfit.android.geocoder.Geocoder;
import com.mapfit.android.geocoder.GeocoderCallback;
import com.mapfit.android.geocoder.model.Address;
import com.mapfit.android.geometry.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dogangulcan on 1/17/18.
 */
public class CoffeeShopActivityJava extends AppCompatActivity {

    private MapfitMap mapfitMap;
    private Repository repository;
    private List<CoffeeShop> coffeeShops;
    private List<Marker> markers = new ArrayList<>();
    private Layer alwaysOpenShopLayer = new Layer();
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_shops);

        init();
        initFilterDrawer();
        initMap();
    }

    private void init() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        repository = new Repository(this);

        FilterAdapter filterAdapter = new FilterAdapter(onFilterCheckedListener);
        filterAdapter.addItems(repository.getFilters());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(filterAdapter);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        coffeeShops = repository.getCoffeeShops();

    }

    private void initMap() {

        ((MapView) findViewById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NotNull MapfitMap mapfitMap) {
                setupMap(mapfitMap);
                addMarkersFromCoffeeShops(coffeeShops);
            }
//                mapfitMap.addPolygon(repository.getLowerManhattanPoly())
//                mapfitMap.addPolyline()
//
//                mapfitMap.getMapOptions().setMaxZoom(55.0f)

        });

        new Geocoder().geocode("119w 24th st new york ny", new GeocoderCallback() {
            @Override
            public void onError(@NotNull String message, @NotNull Exception e) {

            }

            @Override
            public void onSuccess(@NotNull List<Address> addressList) {

            }
        });

    }


    private OnFilterCheckedListener onFilterCheckedListener = new OnFilterCheckedListener() {
        @Override
        public void onDrawRouteClicked() {

        }

        @Override
        public void onSpinnerItemSelected(@NotNull FilterType filterType, @NotNull String string) {

        }

        @Override
        public void onClearMarkersClicked() {

        }

        @Override
        public void onFilterChecked(@NotNull FilterType filterType, boolean isChecked) {
            switch (filterType) {
                case ZOOM_CONTROLS:
//                    mapfitMap.getMapOptions().setPanEnabled(); = isChecked
                    break;
                case MAP_THEME:
                    break;
                case ALL_MARKERS:
                    break;
                case ALWAYS_OPEN:
                    break;
                case CAMERA_STYLE:
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.END);

        }
    };


    private void unusedFunction() {

        mapfitMap.addMarker("119w 24th st new york ny",
                true,
                new OnMarkerAddedCallback() {
                    @Override
                    public void onMarkerAdded(@NotNull Marker marker) {

                    }

                    @Override
                    public void onError(@NotNull Exception exception) {

                    }
                });


        new Geocoder().geocode("119w 24th st new york ny",
                true, new GeocoderCallback() {
                    @Override
                    public void onSuccess(@NotNull List<Address> addressList) {

                    }

                    @Override
                    public void onError(@NotNull String message, @NotNull Exception e) {

                    }
                });


        Float tiltAngle = mapfitMap.getTilt();
        mapfitMap.setTilt(70f);
        LatLng latLng = new LatLng(40.744043, -73.993209);
        mapfitMap.setCenter(latLng);
        Float currentZoomLevel = mapfitMap.getZoom();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(new LatLng(40.744043, -73.993209));
        boundsBuilder.include(new LatLng(40.6902223, -73.9770368));
        boundsBuilder.include(new LatLng(40.7061326, -74.000769));

        LatLngBounds bounds = boundsBuilder.build();
        float paddingPercentage = 0.1f;


        mapfitMap.reCenter();
        mapfitMap.getMapOptions().setMaxZoom(15f);
        mapfitMap.setLatLngBounds(bounds, paddingPercentage);

        LatLngBounds currentMapBounds = mapfitMap.getLatLngBounds();
        Float currentRotation = mapfitMap.getRotation();
        mapfitMap.setRotation(50f);

        mapfitMap.setZoom(16f);
        LatLng centerLatLng = mapfitMap.getCenter();

        mapfitMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public void onMarkerClicked(@NotNull Marker marker) {

            }
        });
        mapfitMap.getMapOptions().setTiltEnabled(false);
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

        mapfitMap.setOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public void onMapLongClicked(@NotNull LatLng latLng) {

            }
        });
        mapfitMap.setOnMapPanListener(new

                                              OnMapPanListener() {
                                                  @Override
                                                  public void onMapPan() {

                                                  }
                                              });
        mapfitMap.setOnMapPinchListener(new

                                                OnMapPinchListener() {
                                                    @Override
                                                    public void onMapPinch() {

                                                    }
                                                });
    }

    private void directions() {
        DirectionsCallback callback = new DirectionsCallback() {

            @Override
            public void onSuccess(@NotNull Route route) {
                // you can draw and show the route now!
            }

            @Override
            public void onError(@NotNull String message, @NotNull Exception e) {
                // handle the error
            }

        };

        new Directions().route(
                "119 W 24th St new york",
                "1000 5th Ave, New York, NY 10028",
                callback);

        Layer layer = new Layer();
        List<Annotation> annotationList = layer.getAnnotations();
        layer.setVisibility(false);
        boolean isLayerVisible = layer.getVisibility();


        new Directions().route(
                new LatLng(40.744043, -73.993209),
                new LatLng(40.7794406, -73.9654327),
                DirectionsType.CYCLING,
                callback);

        List<LatLng> line = new ArrayList<>();

        line.add(new LatLng(40.693825, -73.998691));
        line.add(new LatLng(40.6902223, -73.9770368));
        line.add(new LatLng(40.6930532, -73.9860919));
        line.add(new LatLng(40.7061326, -74.000769));


        Polyline pol = mapfitMap.addPolyline(line);

        pol.setVisibility(false);

        mapfitMap.getDirectionsOptions()
                .setDestination(new LatLng(40.744043, -73.993209))
                .setOrigin(new LatLng(40.7794406, -73.9654327))
                .setType(DirectionsType.CYCLING)
                .showDirections(new DirectionsOptions.RouteDrawCallback() {

                    @Override
                    public void onRouteDrawn(Route route, List<Polyline> legs) {
                        // at this point, the route is drawn on map
                    }

                    @Override
                    public void onError(@NotNull String message, @NotNull Exception e) {
                        // handle the error
                    }

                });


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

        mapfitMap.setOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public void onMapLongClicked(@NotNull LatLng latLng) {

            }
        });

        mapfitMap.setOnMapPanListener(new OnMapPanListener() {
            @Override
            public void onMapPan() {

            }
        });

        mapfitMap.setOnMapPinchListener(new OnMapPinchListener() {
            @Override
            public void onMapPinch() {

            }
        });

        mapfitMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public void onMarkerClicked(@NotNull Marker marker) {

            }
        });

    }

    private void initFilterDrawer() {

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                (Toolbar) findViewById(R.id.toolbar),
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        drawerLayout.addDrawerListener(toggle);

//        nav_view.setNavigationItemSelectedListener(filterClickListener)
    }

    private void setupMap(MapfitMap mapfitMap) {
        this.mapfitMap = mapfitMap;
        mapfitMap.setCenter(new LatLng(40.700798, -74.0050177), 500);
        mapfitMap.setZoom(13f, 500);
        //            setOnMarkerClickListener(onMarkerClickListener)

        mapfitMap.setCenter(new LatLng(40.700798, -74.0050177));


        List<LatLng> poly = new ArrayList<>();

        poly.add(new LatLng(40.693825, -73.998691));
        poly.add(new LatLng(40.6902223, -73.9770368));
        poly.add(new LatLng(40.6930532, -73.9860919));
        poly.add(new LatLng(40.7061326, -74.000769));
        poly.add(new LatLng(40.693825, -73.998691));

        List<List<LatLng>> polygon = new ArrayList();
        polygon.add(poly);
        mapfitMap.addPolygon(polygon);

//        mapfitMap.setLatLngBounds();
    }

    private void addMarkersFromCoffeeShops(List<CoffeeShop> coffeeShops) {

        for (CoffeeShop coffeeShop : coffeeShops) {
            Marker marker = mapfitMap.addMarker(new LatLng(coffeeShop.getLat(), coffeeShop.getLng()));
//            marker.invalidate()

            markers.add(marker);

            // creating a layer of shops that are always open
            if (coffeeShop.getOpen24Hours()) {
                alwaysOpenShopLayer.add(marker);
            }
        }

    }

}
