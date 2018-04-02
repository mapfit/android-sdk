package com.mapfit.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapfit.android.MapTheme
import com.mapfit.android.MapfitMap
import com.mapfit.android.OnMapReadyCallback
import com.mapfit.android.OnMapThemeLoadListener
import com.mapfit.mapfitdemo.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * This example demonstrates how you can implement your own yaml file.
 * You can use Mapfit paint tool to create your own style @see https://mapfit.com/paint/
 *
 * Created by dogangulcan on 3/6/18.
 */
class CustomYamlActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Mapfit.getInstance(this, getString(R.string.mapfit_debug_api_key))
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
                // called when the scene is loaded
            }

            override fun onError() {
                // called when there is an error while loading the scene
            }
        })

        // you can provide a url or a file path here. if your yaml file is in assets folder,
        // just write the filename as the example, assets prefix will be added.
        mapfitMap.getMapOptions().customTheme = "sample.yaml"


    }
}