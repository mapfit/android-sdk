package com.mapfit.mapfitdemo.module.coffeeshop.data

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mapfit.mapfitdemo.data.model.CoffeeShop
import com.mapfit.mapfitdemo.data.model.Filter
import com.mapfit.mapfitdemo.ui.adapter.FilterType
import com.mapfit.mapfitsdk.geometry.LatLng
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

    fun getFilters(): List<Filter> {
        val filters = mutableListOf<Filter>()

        filters.add(Filter(FilterType.CLEAR_MARKERS, "Clear Markers", true))

//        filters.add(Filter(FilterType.ALL_MARKERS, "Markers", true))
//        filters.add(Filter(FilterType.ZOOM_CONTROLS, "Zoom Controls", true))
//        filters.add(Filter(FilterType.MAP_STYLE, "Map Style", true))
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

}