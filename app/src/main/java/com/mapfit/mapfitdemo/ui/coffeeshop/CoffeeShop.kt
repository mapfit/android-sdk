package com.mapfit.mapfitdemo.ui.coffeeshop

import com.mapfit.mapfitsdk.annotations.Marker

/**
 * Created by dogangulcan on 12/22/17.
 */
data class CoffeeShop(var id: String,
                      var lat: Double,
                      var lon: Double,
                      var address: String,
                      var title: String,
                      var open24Hours: Boolean,
                      var opensAt: String,
                      var closesAt: String) {

    var marker: Marker? = null

}