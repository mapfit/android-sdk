package com.mapfit.mapfitdemo.module.coffeeshop.data

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mapfit.android.geometry.LatLng
import com.mapfit.mapfitdemo.data.model.CoffeeShop
import com.mapfit.mapfitdemo.data.model.Filter
import com.mapfit.mapfitdemo.ui.adapter.FilterType
import java.io.IOException
import java.lang.reflect.Modifier
import java.nio.charset.Charset


/**
 * Created by dogangulcan on 12/22/17.
 */
class Repository(private val context: Context) {

    fun getCoffeeShops(): List<CoffeeShop> {

        val gson = GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .create()

        val listType = object : TypeToken<List<CoffeeShop>>() {}.type
        val coffeeShopList: List<CoffeeShop>? = gson.fromJson(getCoffeeShopsJson(), listType)

        return coffeeShopList ?: emptyList()
    }

    private fun getCoffeeShopsJson(): String {

        return try {
            val `is` = context.assets.open("dummy_coffee_shops.json")
//            val `is` = context.assets.open("100_vendor.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()

            String(buffer, Charset.forName("UTF-8"))

        } catch (ex: IOException) {
            ex.printStackTrace()
            ""
        }

    }

    fun getFilters(): List<Filter<*>> {
        val filters = mutableListOf<Filter<*>>()

        filters.add(Filter<Any>(FilterType.CLEAR_MARKERS, "Clear Markers", true))

//        filters.add(Filter(FilterType.ALL_MARKERS, "Markers", true))
        filters.add(Filter(FilterType.ZOOM_CONTROLS, "Zoom Controls", false, null))
        filters.add(Filter(FilterType.COFFEE_SHOPS, "Coffee Shops", true, null))
        filters.add(Filter(FilterType.RECENTER, "Re-center", false, null))
        filters.add(Filter(FilterType.COMPASS, "Compass", false, null))
        filters.add(Filter(FilterType.PAN_GESTURE, "Pan Gesture", true, null))
        filters.add(Filter(FilterType.ROTATE_GESTURE, "Rotate Gesture", true, null))
        filters.add(Filter(FilterType.PINCH_GESTURE, "Pinch Gesture", true, null))
        filters.add(Filter(FilterType.TILT_GESTURE, "Tilt Gesture", true, null))
        filters.add(Filter(FilterType.DRAW_ROUTE, "Draw Route", true, null))


        filters.add(
            Filter(
                FilterType.MAP_THEME,
                "Map Theme",
                true,
                listOf("MAPFIT_DAY", "MAPFIT_NIGHT", "MAPFIT_GRAYSCALE")
            )
        )
//        filters.add(Filter(FilterType.CAMERA_STYLE, "Camera Style", true))
//        filters.add(Filter(FilterType.ALWAYS_OPEN, "Always Open Vendors", false))

        return filters
    }

    fun getLowerManhattanPoly(): List<List<LatLng>> {

        val list = mutableListOf<List<LatLng>>()
        val subList = mutableListOf<LatLng>()


        subList.add(LatLng(40.693825, -73.998691))
        subList.add(LatLng(40.6902223, -73.9770368))
        subList.add(LatLng(40.6930532, -73.9860919))
        subList.add(LatLng(40.7061326, -74.000769))
        subList.add(LatLng(40.7170279, -74.0072867))
        subList.add(LatLng(40.693825, -73.998691))



        list.add(subList)

        return list
    }

    fun getLowerManhattanPolyline(): List<LatLng> {

        val list = mutableListOf<LatLng>()

        list.add(LatLng(40.693825, -73.998691))
        list.add(LatLng(40.6902223, -73.9770368))
        list.add(LatLng(40.6930532, -73.9860919))
        list.add(LatLng(40.7061326, -74.000769))


        return list
    }

}