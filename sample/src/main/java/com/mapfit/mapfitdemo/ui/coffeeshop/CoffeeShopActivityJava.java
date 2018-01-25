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

import com.mapfit.mapfitdemo.R;
import com.mapfit.mapfitdemo.data.model.CoffeeShop;
import com.mapfit.mapfitdemo.module.coffeeshop.data.Repository;
import com.mapfit.mapfitdemo.ui.adapter.FilterAdapter;
import com.mapfit.mapfitdemo.ui.adapter.FilterType;
import com.mapfit.mapfitdemo.ui.adapter.OnFilterCheckedListener;
import com.mapfit.mapfitsdk.Layer;
import com.mapfit.mapfitsdk.MapView;
import com.mapfit.mapfitsdk.MapfitMap;
import com.mapfit.mapfitsdk.OnMapReadyCallback;
import com.mapfit.mapfitsdk.annotations.Marker;
import com.mapfit.mapfitsdk.geometry.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dogangulcan on 1/17/18.
 */
class CoffeeShopActivityJava extends AppCompatActivity {

    private MapfitMap mapfitMap;
    private Repository repository = new Repository(this);
    private List<CoffeeShop> coffeeShops = repository.getCoffeeShops();
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

        FilterAdapter filterAdapter = new FilterAdapter(onFilterCheckedListener);
        filterAdapter.addItems(repository.getFilters());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(filterAdapter);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));

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
    }


    private OnFilterCheckedListener onFilterCheckedListener = new OnFilterCheckedListener() {
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


    }


    private void addMarkersFromCoffeeShops(List<CoffeeShop> coffeeShops) {

        for (CoffeeShop coffeeShop : coffeeShops) {
            Marker marker = mapfitMap.addMarker(new LatLng(coffeeShop.getLat(), coffeeShop.getLon()));
//            marker.invalidate()

            markers.add(marker);

            // creating a layer of shops that are always open
            if (coffeeShop.getOpen24Hours()) {
                alwaysOpenShopLayer.add(marker);
            }
        }

    }

}
