package com.mapfit.mapfitsdk

import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.mapfit.mapfitsdk.geometry.LatLng
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

/**
 * Unit tests for [Layer].
 *
 * Created by dogangulcan on 1/5/18.
 */
@RunWith(AndroidJUnit4::class)
class LayerTest {

    private lateinit var mapfitMap: MapfitMap

    private lateinit var mapfitMap2: MapfitMap

    private val mMockContext = InstrumentationRegistry.getContext()
    private val latLng = LatLng(40.693825, -73.998691)


    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        MapView(mMockContext).getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@LayerTest.mapfitMap = mapfitMap
            }
        })

        MapView(mMockContext).getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@LayerTest.mapfitMap2 = mapfitMap
            }
        })
    }

    @Test
    @UiThreadTest
    fun testLayerDefaults() {
        val layer = Layer()
        Assert.assertTrue(layer.isVisible)
    }

    @Test
    @UiThreadTest
    fun testAdding() {

        // create marker, add to a layer, add layer to another map

        val marker = mapfitMap.addMarker(latLng)
        val layer = Layer()
        layer.add(marker)
        mapfitMap2.addLayer(layer)

    }


    @Test
    @UiThreadTest
    fun testRemovingLayerFromAMap() {
        // all markers on the map that are in layer should be removed from the map
    }

    @Test
    @UiThreadTest
    fun testDisposingLayer() {
        // all markers should be removed from all maps that layer is attached to
    }

    @Test
    @UiThreadTest
    fun testDuplicates() {
        // adding layer to a map has same marker should not duplicate

    }

    @Test
    @UiThreadTest
    fun testClear() {
        val latLng = LatLng(40.693825, -73.998691)
        val marker = mapfitMap.addMarker(latLng)

        val layer = Layer()

        layer.add(marker)
        layer.add(marker)
        layer.add(marker)
        layer.add(marker)

        layer.clear()
        Assert.assertTrue(layer.annotations.size == 0)
    }


}