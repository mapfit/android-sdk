package com.mapfit.mapfitdemo.ui.coffeeshop

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.mapfit.mapfitdemo.R
import com.mapfit.mapfitdemo.data.model.CoffeeShop
import com.mapfit.mapfitdemo.module.coffeeshop.data.Repository
import com.mapfit.mapfitdemo.ui.adapter.FilterAdapter
import com.mapfit.mapfitdemo.ui.adapter.FilterType
import com.mapfit.mapfitdemo.ui.adapter.OnFilterCheckedListener
import com.mapfit.mapfitsdk.annotations.*
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerClickListener
import com.mapfit.mapfitsdk.geometry.LatLng
import kotlinx.android.synthetic.main.activity_coffee_shops.*
import kotlinx.android.synthetic.main.app_bar_coffee_shops.*
import kotlinx.android.synthetic.main.content_coffee_shops.*
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import com.mapfit.mapfitsdk.*
import kotlinx.android.synthetic.main.content_coffee_shops.view.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch


/**
 * Activity where coffee shops are shown on the map.
 *
 * Created by dogangulcan on 12/21/17.
 */
class CoffeeShopActivity : AppCompatActivity() {

    private lateinit var mapfitMap: MapfitMap
    private val repository = Repository(this)
    private val coffeeShops: List<CoffeeShop>? by lazy { repository.getCoffeeShops() }
    private var markers: MutableList<Marker> = mutableListOf()
    private var alwaysOpenShopLayer = Layer()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var drawerLayout: DrawerLayout
    private var bottomSheetHideJob = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coffee_shops)

        init()
        initFilterDrawer()
        initMap()
    }

    private fun init() {
        setSupportActionBar(toolbar)

        val filterAdapter = FilterAdapter(onFilterCheckedListener)
        filterAdapter.addItems(repository.getFilters())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = filterAdapter

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)

    }

    private fun initMap() {
        map.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                setupMap(mapfitMap)
//                coffeeShops?.let { addMarkersFromCoffeeShops(it) }
//                mapfitMap.addPolygon(repository.getLowerManhattanPoly())
//                mapfitMap.addPolyline()
//
//                mapfitMap.getMapOptions().setMaxZoom(55.0f)
            }
        })
    }

    private val onFilterCheckedListener = object : OnFilterCheckedListener {
        override fun onFilterChecked(filterType: FilterType, isChecked: Boolean) {
            when (filterType) {
                FilterType.ZOOM_CONTROLS -> {
//                    mapfitMap.getMapOptions().isZoomControlsVisible = isChecked
                }
            }

            drawerLayout.closeDrawer(GravityCompat.END)
        }
    }

    private fun initFilterDrawer() {

        drawerLayout = drawer_layout

        val toggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = false
        drawerLayout.addDrawerListener(toggle)

//        nav_view.setNavigationItemSelectedListener(filterClickListener)
    }

    private fun setupMap(mapfitMap: MapfitMap) {
        this@CoffeeShopActivity.mapfitMap = mapfitMap

        mapfitMap.apply {
            setCenter(LatLng(40.700798, -74.0050177), 500)
            setZoom(13f, 500)
//            setOnMarkerClickListener(onMarkerClickListener)
        }

    }

    private val onMarkerClickListener = object : OnMarkerClickListener {
        override fun onMarkerClicked(marker: Marker) {
            marker.setIcon(MapfitMarker.DARK_AUTO)
            mapfitMap.setCenter(marker.position, 300)

            marker.data?.let {
                bottom_sheet.txtTitle.text = (it as CoffeeShop).title
                bottom_sheet.txtSubTitle.text = (it).address
            }


            bottomSheetHideJob.cancel()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetHideJob = launch {
                delay(1500)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

        }
    }

    private fun addMarkersFromCoffeeShops(coffeeShops: List<CoffeeShop>) {

//        coffeeShops.forEach { shop ->
//
//            val marker = mapfitMap.addMarker(LatLng(shop.lat, shop.lon))
//                    .setIcon(MapfitMarker.DARK_CAFE)
//                    .setData(shop)
//
//            markers.add(marker)
//
//            // creating a layer of shops that are always open
//            if (shop.open24Hours) {
//                alwaysOpenShopLayer.add(marker)
//            }
//
//        }

    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map.onLowMemory()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.coffee_shops, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_filter -> drawerLayout.openDrawer(GravityCompat.END)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}