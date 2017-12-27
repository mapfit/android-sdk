package com.mapfit.mapfitdemo.repo

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mapfit.mapfitdemo.ui.coffeeshop.CoffeeShop
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

    fun getCoffeeShopsJson(): String {

        return try {
            val `is` = context.assets.open("dummy_coffee_shops.json")
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

}