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

    private lateinit var mapView: com.mapfit.mapfitsdk.MapView
    private lateinit var mapfitMap: MapfitMap

    private val mMockContext = InstrumentationRegistry.getContext()


    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        mapView = MapView(mMockContext)

        mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@LayerTest.mapfitMap = mapfitMap
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

    @Test
    @UiThreadTest
    fun testAddRemove() {
//        val latLng = LatLng(40.693825, -73.998691)
//        val marker = mapfitMap.addMarker(latLng)
//
//        val layer = Layer()
//
//        layer.add(marker)
//
//        val sameMarker = layer.annotations.find { it == marker }
//        Assert.assertTrue(sameMarker != null)
//
//        layer.remove(marker)
//        val sameMarkerCandidate = layer.annotations.find { it == marker }
//        Assert.assertTrue(sameMarkerCandidate == null)

    }

}