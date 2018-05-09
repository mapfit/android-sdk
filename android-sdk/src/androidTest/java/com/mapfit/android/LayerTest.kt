package com.mapfit.android

import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.annotations.Marker
import com.mapfit.android.annotations.MarkerOptions
import com.mapfit.android.geometry.LatLng
import junit.framework.Assert
import org.junit.After
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
        Mapfit.getInstance(mMockContext, mMockContext.getString(R.string.mapfit_debug_api_key))
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

    @After
    fun dispose() {
        Mapfit.dispose()
    }

    @Test
    @UiThreadTest
    fun testLayerDefaults() {
        val layer = Layer()
        Assert.assertTrue(layer.visibility)
    }

    @Test
    @UiThreadTest
    fun testAddingMarkerToLayer() {
        val (marker, layer) = addLayerWithMarkerToMaps()
        layer.add(marker)
        Assert.assertEquals(2, marker.mapBindings.size)
    }

    @Test
    @UiThreadTest
    fun testAddingLayerToSecondMap() {
        val (marker, layer) = addLayerWithMarkerToMaps()
        Assert.assertEquals(2, marker.mapBindings.size)
        Assert.assertEquals(1, layer.annotations.size)
    }

    @Test
    @UiThreadTest
    fun testRemovingMarkerFromLayer() {
        val (marker, layer) = addLayerWithMarkerToMaps()
        layer.remove(marker)
        Assert.assertEquals(0, marker.mapBindings.size)
        Assert.assertEquals(0, layer.annotations.size)
    }

    @Test
    @UiThreadTest
    fun testRemovingMarker() {
        val (marker, layer) = addLayerWithMarkerToMaps()
        marker.remove()
        Assert.assertEquals(0, marker.mapBindings.size)
        Assert.assertEquals(0, layer.annotations.size)
    }


    @Test
    @UiThreadTest
    fun testRemovingLayerFromAMap() {
        val (marker, layer) = addLayerWithMarkerToMaps()
        Assert.assertEquals(2, marker.mapBindings.size)
        mapfitMap.removeLayer(layer)
        Assert.assertEquals(1, marker.mapBindings.size)
    }

    @Test
    @UiThreadTest
    fun testDisposingLayer() {
        val (marker, layer) = addLayerWithMarkerToMaps()
        layer.clear()
        Assert.assertEquals(0, marker.mapBindings.size)
    }

    private fun addLayerWithMarkerToMaps(): Pair<Marker, Layer> {
        val marker = createMarker()
        val layer = Layer()
        layer.add(marker)
        mapfitMap.addLayer(layer)
        mapfitMap2.addLayer(layer)
        return Pair(marker, layer)
    }


    @Test
    @UiThreadTest
    fun testClear() {
        val latLng = LatLng(40.693825, -73.998691)
        val marker = mapfitMap.addMarker(MarkerOptions().position(latLng))
        val layer = Layer()
        layer.add(marker)
        layer.add(marker)
        layer.add(marker)
        layer.add(marker)
        layer.clear()
        Assert.assertTrue(layer.annotations.size == 0)
    }

    private fun createMarker() = mapfitMap.addMarker(MarkerOptions().position(latLng))


}