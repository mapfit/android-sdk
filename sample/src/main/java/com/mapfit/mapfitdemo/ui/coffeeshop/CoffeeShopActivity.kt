package com.mapfit.mapfitdemo.ui.coffeeshop

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.mapfit.mapfitdemo.R
import com.mapfit.mapfitdemo.data.model.CoffeeShop
import com.mapfit.mapfitdemo.module.coffeeshop.data.Repository
import com.mapfit.mapfitdemo.ui.adapter.FilterAdapter
import com.mapfit.mapfitdemo.ui.adapter.FilterType
import com.mapfit.mapfitdemo.ui.adapter.OnFilterCheckedListener
import com.mapfit.mapfitsdk.*
import com.mapfit.mapfitsdk.annotations.MapfitMarker
import com.mapfit.mapfitsdk.annotations.Marker
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerAddedCallback
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerClickListener
import com.mapfit.mapfitsdk.directions.DirectionsApi
import com.mapfit.mapfitsdk.directions.DirectionsCallback
import com.mapfit.mapfitsdk.directions.DirectionsType
import com.mapfit.mapfitsdk.directions.model.Route
import com.mapfit.mapfitsdk.geocoder.GeocoderApi
import com.mapfit.mapfitsdk.geocoder.GeocoderCallback
import com.mapfit.mapfitsdk.geocoder.model.Address
import com.mapfit.mapfitsdk.geometry.LatLng
import com.mapfit.mapfitsdk.geometry.LatLngBounds
import com.mapfit.mapfitsdk.utils.decodePolyline
import kotlinx.android.synthetic.main.activity_coffee_shops.*
import kotlinx.android.synthetic.main.app_bar_coffee_shops.*
import kotlinx.android.synthetic.main.content_coffee_shops.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch


/**
 * Activity where coffee shops are shown on the map.
 *
 * Created by dogangulcan on 12/21/17.
 */
class CoffeeShopActivity : AppCompatActivity() {

    private lateinit var mapfitMap: MapfitMap
    private lateinit var mapfitMap2: MapfitMap
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
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(toolbar.windowToken, 0)

        val filterAdapter = FilterAdapter(onFilterCheckedListener)
        filterAdapter.addItems(repository.getFilters())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = filterAdapter

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)

    }

    private fun initMap() {
        map.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                setupMap(mapfitMap)
//                mapfitMap.addPolygon(repository.getLowerManhattanPoly())
//                mapfitMap.addPolyline()
//                mapfitMap.getMapOptions().setMaxZoom(55.0f)
            }
        })

        map2.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                setupMap2(mapfitMap)

            }
        })
    }

    private fun setupMap2(mapfitMap: MapfitMap) {
        this.mapfitMap2 = mapfitMap
        mapfitMap2.addLayer(alwaysOpenShopLayer)

        mapfitMap2.apply {
            setCenter(LatLng(40.700798, -74.0050177), 500)
            setZoom(13f, 500)
        }


        mapfitMap2.setOnPlaceInfoClickListener(object : MapfitMap.OnPlaceInfoClickListener {
            override fun onPlaceInfoClicked(marker: Marker) {

//                marker.setIcon("https://darley-cpl.netdna-ssl.com/sites/default/files/styles/stallion_thumbnail/public/drupal-media/stallion-images/Australia-2016/exceed-and-excel/square-Exceed_And_Excel_0001_Thoroughbred_stallion.jpg?itok=ByfQuwCC")

//                alwaysOpenShopLayer.remove(marker) // marker will be removed from everywhere that layer has //WORKS
//                mapfitMap.removeMarker(marker) // marker is removed from map, will exist on others //WORKS
                marker.remove() // marker will be removed from everywhere //WORKS
//                this@CoffeeShopActivity.mapfitMap.removeLayer(alwaysOpenShopLayer) // WORKS
//                alwaysOpenShopLayer.clear() // WORKS

            }
        })

    }


    private fun drawRouteWithMapView() {


        mapfitMap.getDirectionsOptions()
            .setDestination(LatLng(40.744255, -73.993774))
            .setOrigin(LatLng(40.575534, -73.961857))
            .setType(DirectionsType.DRIVING)
            .showDirections(object : DirectionsOptions.RouteDrawCallback {
                override fun onRouteDrawn(route: Route) {
                    Toast.makeText(
                        this@CoffeeShopActivity,
                        "Route is drawn!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(message: String, e: Exception) {
                    e.printStackTrace()
                }
            })
    }

    private val onFilterCheckedListener = object : OnFilterCheckedListener {

        override fun onDrawRouteClicked() {
//            drawRouteWithMapView()

            val callback = object : DirectionsCallback {
                override fun onSuccess(route: Route) {
                    drawRoute(route)

                }

                override fun onError(message: String, e: Exception) {
                    e.printStackTrace()
                }
            }

            DirectionsApi().getDirections(
                originAddress = "119 W 24th St new york",
                destinationAddress = "107 Sacred Heart Ln baltimore",
                callback = callback
            )

            drawerLayout.closeDrawer(GravityCompat.END)
        }

        override fun onSpinnerItemSelected(filterType: FilterType, string: String) {
            when (filterType) {
                FilterType.MAP_THEME -> {
                    mapfitMap.getMapOptions().theme = MapTheme.valueOf(string)
                    drawerLayout.closeDrawer(GravityCompat.END)
                }
            }
        }

        override fun onClearMarkersClicked() {
            markers.forEach { it.remove() }
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        override fun onFilterChecked(filterType: FilterType, isChecked: Boolean) {
            when (filterType) {
                FilterType.ZOOM_CONTROLS -> mapfitMap.getMapOptions().zoomControlsEnabled =
                        isChecked
                FilterType.COMPASS -> mapfitMap.getMapOptions().compassButtonEnabled = isChecked
                FilterType.RECENTER -> mapfitMap.getMapOptions().recenterButtonEnabled =
                        isChecked
                FilterType.PAN_GESTURE -> mapfitMap.getMapOptions().panEnabled = isChecked
                FilterType.ROTATE_GESTURE -> mapfitMap.getMapOptions().rotateEnabled = isChecked
                FilterType.PINCH_GESTURE -> mapfitMap.getMapOptions().pinchEnabled = isChecked
                FilterType.TILT_GESTURE -> mapfitMap.getMapOptions().tiltEnabled = isChecked
            }

            drawerLayout.closeDrawer(GravityCompat.END)
        }
    }

    private fun drawRoute(route: Route) {
        async {
            val destinationLocation =
                LatLng(route.destinationLocation[1], route.destinationLocation[0])
            val originLocation = LatLng(route.sourceLocation[1], route.sourceLocation[0])
            val startMarker =
                mapfitMap2.addMarker(destinationLocation).setIcon(MapfitMarker.LIGHT_COOKING)
            val endMarker=mapfitMap2.addMarker(originLocation).setIcon(MapfitMarker.DARK_BAR)

            mapfitMap2.setCenter(originLocation, 200)

            route.trip.legs.forEach {
                val line = decodePolyline(it.shape)
                mapfitMap2.addPolyline(line)
            }
        }

    }

    private fun initFilterDrawer() {

        drawerLayout = drawer_layout

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = false
        drawerLayout.addDrawerListener(toggle)

//        nav_view.setNavigationItemSelectedListener(filterClickListener)
    }

    private fun setupMap(mapfitMap: MapfitMap) {
        this.mapfitMap = mapfitMap

        mapfitMap.apply {
            setCenter(LatLng(40.700798, -74.0050177), 500)
            setZoom(13f, 500)

//            boundaryBuilder()
//            addMapfitOfficeWithGeocoder()
            setupMarkerWithAddressInput()
            coffeeShops?.let { addMarkersFromCoffeeShops(mapfitMap, it) }
//            setMapBoundsToColorado()
//            setMapBoundsToUtah()
            setOnMarkerClickListener(onMarkerClickListener)
            setOnMapLongClickListener(onMapLongClickListener)

            setOnPlaceInfoClickListener(object : MapfitMap.OnPlaceInfoClickListener {
                override fun onPlaceInfoClicked(marker: Marker) {
                    Toast.makeText(
                        this@CoffeeShopActivity,
                        "Place info is clicked!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            // calling inflate without a root ignores layout params
            val customView = LayoutInflater.from(this@CoffeeShopActivity)
                .inflate(
                    R.layout.widget_custom_place_info,
                    findViewById(R.id.drawer_layout),
                    false
                )

//            setPlaceInfoAdapter(object : MapfitMap.PlaceInfoAdapter {
//                override fun getPlaceInfoView(marker: Marker): View {
//
//                    customView.img.setImageResource(R.drawable.ic_watermark_light)
//                    customView.txtTitle.text = marker.title
//
//                    return customView
//                }
//            })


//

        }
        val polyline = mapfitMap.addPolyline(repository.getLowerManhattanPolyline())
        alwaysOpenShopLayer.add(polyline)
    }

    private fun boundaryBuilder() {
        val latLngList = listOf(
            LatLng(37.198504, -83.272133),
            LatLng(29.652243, -29.042111),
            LatLng(38.246623, -82.737144),
            LatLng(36.691771, -110.030517),
            LatLng(37.940202, -107.461721),
            LatLng(39.400789, -80.243273)
        )

        val boundsBuilder = LatLngBounds.Builder()

        latLngList.forEach {
            mapfitMap.addMarker(it)
            boundsBuilder.include(it)
        }

        val bounds = boundsBuilder.build()
        mapfitMap.setBounds(bounds, .8f)
        mapfitMap.addMarker(bounds.southWest).setIcon(MapfitMarker.DARK_BAR)
        mapfitMap.addMarker(bounds.northEast).setIcon(MapfitMarker.DARK_AIRPORT)

    }

    private fun setMapBoundsToColorado() {
        val ne = LatLng(40.991366, -102.068297)
        val sw = LatLng(37.040997, -108.995177)
        val bounds = LatLngBounds(ne, sw)

        mapfitMap.addMarker(ne)
        mapfitMap.addMarker(sw)
        mapfitMap.setBounds(bounds, 1f)

//        launch {
//            delay(2000)
//            val afterBounds = mapfitMap.getBounds()
//            delay(5000)
//            mapfitMap.setBounds(afterBounds)
//        }

    }

    private fun setMapBoundsToUtah() {
        val ne = LatLng(38.334804, -111.245101)
        val sw = LatLng(38.326325, -111.351381)
        val bounds = LatLngBounds(ne, sw)

        mapfitMap.addMarker(ne)
        mapfitMap.addMarker(sw)
        mapfitMap.setBounds(bounds, 1f)

    }

    private val onMapLongClickListener = object : OnMapLongClickListener {
        override fun onMapLongClicked(latLng: LatLng) {
        }
    }

    private val onMarkerClickListener = object : OnMarkerClickListener {
        override fun onMarkerClicked(marker: Marker) {
//            marker.setIcon(MapfitMarker.DARK_AUTO)
            mapfitMap.setCenter(marker.getPosition(), 300)
            mapfitMap.getBounds().center

//            marker.data?.let {
//                bottom_sheet.txtTitle.text = (it as CoffeeShop).title
//                bottom_sheet.txtSubTitle.text = (it).address
//            }

//            bottomSheetHideJob.cancel()
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            bottomSheetHideJob = launch {
//                delay(1500)
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//            }

        }
    }

    private fun setupMarkerWithAddressInput() {

        edtAddress.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                addMarkerWithAddress()
                return@OnEditorActionListener true
            }
            false
        })

        btnAddMarker.setOnClickListener {
            addMarkerWithAddress()
        }
    }

    private fun addMarkerWithAddress() {

        val address = edtAddress.text.toString()
        if (address.isBlank()) {

        } else {

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(toolbar.windowToken, 0)

            mapfitMap.addMarker(address, object : OnMarkerAddedCallback {

                override fun onMarkerAdded(marker: Marker) {
                    mapfitMap.setCenter(marker.getPosition(), 300)
                    mapfitMap.setZoom(17f, 300)
                    edtAddress.setText("")
                    markers.add(marker)

                }

                override fun onError(exception: Exception) {
                    Toast.makeText(
                        this@CoffeeShopActivity,
                        "Couldn't find a valid location for given address",
                        Toast.LENGTH_LONG
                    ).show()

                }

            })
        }
    }

    private fun addMarkersFromCoffeeShops(mapfitMap: MapfitMap, coffeeShops: List<CoffeeShop>) {

        coffeeShops.forEach { shop ->

            val marker = mapfitMap.addMarker(LatLng(shop.lat, shop.lon))
//            marker.invalidate()

            val markerIcon = when (shop.id) {
                "vendor0001" -> MapfitMarker.DARK_COMMUNITY
                "vendor0002" -> MapfitMarker.DARK_ARTS
                "vendor0003" -> MapfitMarker.DARK_COOKING
                "vendor0004" -> MapfitMarker.DARK_SPORTS
                "vendor0005" -> MapfitMarker.LIGHT_HOMEGARDEN
                "vendor0006" -> MapfitMarker.LIGHT_HOTEL
                "vendor0007" -> MapfitMarker.DARK_MEDICAL
                else -> MapfitMarker.LIGHT_DEFAULT
            }

            marker.setIcon(markerIcon)

            marker.title = shop.title
            marker.subtitle1 = shop.address
            marker.subtitle2 = shop.id

            markers.add(marker)

            // creating a layer of shops that are always open
            if (shop.open24Hours) {
                alwaysOpenShopLayer.add(marker)
            }

        }

        val westFortGreen = LatLng(40.689837, -73.982629)
        launch {
            delay(20000)
//            markers[1].setIcon("https://darley-cpl.netdna-ssl.com/sites/default/files/styles/stallion_thumbnail/public/drupal-media/stallion-images/Australia-2016/exceed-and-excel/square-Exceed_And_Excel_0001_Thoroughbred_stallion.jpg?itok=ByfQuwCC")
//            markers[1].setPosition(westFortGreen)
        }


    }

    private fun addMapfitOfficeWithGeocoder() {
        GeocoderApi().geocodeAddress("119w 24th st new york ny",
            object : GeocoderCallback {

                override fun onError(message: String, e: Exception) {
                    print(e)
                }

                override fun onSuccess(addressList: List<Address>) {
                    addressList.forEach { address ->
                        if (address.entrances.isNotEmpty()) {
                            address.entrances.forEach {
                                markers.add(
                                    mapfitMap.addMarker(
                                        LatLng(
                                            it.latitude,
                                            it.longitude
                                        )
                                    )
                                )
                            }
                        } else {
                            markers.add(
                                mapfitMap.addMarker(
                                    LatLng(
                                        address.latitude,
                                        address.longitude
                                    )
                                )
                            )
                        }
                    }
                }
            })
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
        menuInflater.inflate(R.menu.coffee_shops, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_filter -> {
                drawerLayout.openDrawer(GravityCompat.END)
            }
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
