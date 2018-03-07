package com.mapfit.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.*
import com.mapfit.mapfitdemo.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_showcase.*

/**
 * Created by dogangulcan on 3/6/18.
 */
class CustomYamlActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapfit.getInstance(this, getString(R.string.mapfit_debug_api_key))
        setContentView(R.layout.activity_main)

        mapView.getMapAsync(
            MapTheme.MAPFIT_GRAYSCALE,
            onMapReadyCallback = object : OnMapReadyCallback {
                override fun onMapReady(mapfitMap: MapfitMap) {
                    setCustomTheme(mapfitMap)
                }
            })

    }

    private fun setCustomTheme(mapfitMap: MapfitMap) {

        mapfitMap.setOnMapThemeLoadListener(object : OnMapThemeLoadListener {
            override fun onLoaded() {

            }

            override fun onError() {

            }
        })

        mapfitMap.getMapOptions().customTheme = "sample.yaml"
//        mapfitMap.getMapOptions().customTheme = "https://cdn.mapfit.com/m1/themes/mapfit-greyscale.yaml"
//        mapfitMap.getMapOptions().customTheme = "https://cdn.mapfit.com/m1/themes/mapfit-grayscale.yaml"

//        val yamlFilePath = "file://data/data/com.sample.app/sample.yaml"
//        mapfitMap.getMapOptions().customTheme = yamlFilePath
//        mapfitMap.getMapOptions().customTheme = "5"
////
//        val yamlUrl = "https://mydomain.com/sample.yaml"
//        mapfitMap.getMapOptions().customTheme = yamlUrl

    }
}