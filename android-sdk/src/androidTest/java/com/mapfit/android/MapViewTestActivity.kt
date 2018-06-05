package com.mapfit.android

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.mapfit.android.*

/**
 * Dummy activity for testing [MapView].
 *
 * Created by dogangulcan on 1/26/18.
 */
class MapViewTestActivity : AppCompatActivity() {

    val idlingResource by lazy { CountingIdlingResource("dummy_resource", true) }
    lateinit var mapfitMap: MapfitMap
    lateinit var mapView: MapView

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instantiateMapfit(this)
        mapView = MapView(this)
        mapView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mapView.id = R.id.mapView
        setContentView(mapView)
    }

    fun init() {
        idlingResource.increment()
        mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@MapViewTestActivity.mapfitMap = mapfitMap
                idlingResource.decrement()
            }
        })
    }

    fun initWithCustomYaml() {
        idlingResource.increment()
        mapView.getMapAsync(
            "https://cdn.mapfit.com/v2-3/themes/mapfit-night.yaml",
            onMapReadyCallback = object : OnMapReadyCallback {
                override fun onMapReady(mapfitMap: MapfitMap) {
                    this@MapViewTestActivity.mapfitMap = mapfitMap
                    idlingResource.decrement()
                }
            })
    }

    val onMapPinchListener = object : OnMapPinchListener {
        override fun onMapPinch() {
            idlingResource.decrement()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Mapfit.dispose()
    }

}