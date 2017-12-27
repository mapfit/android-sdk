package com.mapfit.mapfitdemo.ui.coffeeshop

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.mapfit.mapfitdemo.R
import com.mapfit.mapfitdemo.repo.Repository
import com.mapfit.mapfitsdk.MapfitMap
import com.mapfit.mapfitsdk.OnMapReadyCallback
import com.mapfit.mapfitsdk.annotations.Annotation
import com.mapfit.mapfitsdk.annotations.AnnotationClickListener
import com.mapfit.mapfitsdk.annotations.Layer
import com.mapfit.mapfitsdk.annotations.Marker
import com.mapfit.mapfitsdk.annotations.base.MapfitMarker
import com.mapfit.mapfitsdk.geo.LatLng
import kotlinx.android.synthetic.main.activity_coffee_shops.*
import kotlinx.android.synthetic.main.app_bar_coffee_shops.*
import kotlinx.android.synthetic.main.content_coffee_shops.*

/**
 * Activity where coffee shops are shown on the map.
 *
 * Created by dogangulcan on 12/21/17.
 */
class CoffeeShopActivity : AppCompatActivity() {

    private lateinit var mapfitMap: MapfitMap
    private val repository = Repository(this)
    val coffeeShops: List<CoffeeShop>? by lazy { repository.getCoffeeShops() }
    private var markers: MutableList<Marker> = mutableListOf()
    private var alwaysOpenShopLayer = Layer("always_open")

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coffee_shops)

        init()
        initFilterDrawer()
        initMap()
    }

    private fun init() {
        setSupportActionBar(toolbar)
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

    private fun initMap() {
        map.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                setupMap(mapfitMap)
                coffeeShops?.let { addMarkersFromCoffeeShops(it) }
            }
        })
    }

    private fun setupMap(mapfitMap: MapfitMap) {
        this@CoffeeShopActivity.mapfitMap = mapfitMap

        mapfitMap.apply {
            setCenter(LatLng(40.700798, -74.0050177), 500)
            setZoomLevel(13f, 500)

            setOnAnnotationClickListener(object : AnnotationClickListener {
                override fun onAnnotationClick(annotation: Annotation) {
                    when (annotation) {
                        is Marker -> moveMapToMarker(annotation)

                    }
                }
            })
        }
    }

    private fun moveMapToMarker(marker: Marker) {
        mapfitMap.setCenter(marker.getLocation())
    }

    fun addMarkersFromCoffeeShops(coffeeShops: List<CoffeeShop>) {

        coffeeShops.forEach { shop ->

            val marker = mapfitMap.addMarker(LatLng(shop.lat, shop.lon))
                    .setIcon(MapfitMarker.DARK_CAFE)

            markers.add(marker)

            // creating a layer of shops that are always open
            if (shop.open24Hours) {
                alwaysOpenShopLayer.add(marker)
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.coffee_shops, menu)
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

    private val filterClickListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_camera -> {
            }

        }

        drawerLayout.closeDrawer(GravityCompat.END)
        true
    }

}