package com.mapfit.mapfitsdk.annotations

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.mapfit.mapfitsdk.MapView
import com.mapfit.mapfitsdk.Mapfit
import com.mapfit.mapfitsdk.MapfitMap
import com.mapfit.mapfitsdk.OnMapReadyCallback
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerAddedCallback
import com.mapfit.mapfitsdk.geometry.LatLng
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for [Marker] functionality.
 *
 * Created by dogangulcan on 1/17/18.
 */
@RunWith(AndroidJUnit4::class)
class MarkerTest {

    private val mMockContext: Context = InstrumentationRegistry.getContext()

    private lateinit var mapfitMap: MapfitMap

    @Mock
    private lateinit var onMarkerAddedCallback: OnMarkerAddedCallback

    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        Mapfit.getInstance(mMockContext, "591dccc4e499ca0001a4c6a41a2ed1be54804856508265221862231b")
        val mapView = MapView(mMockContext, null)

        mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@MarkerTest.mapfitMap = mapfitMap
            }
        })
    }

    @Test
    @UiThreadTest
    fun testMarkerPosition() {

        // initial position
        val latLng = LatLng(40.693825, -73.998691)
        val marker = mapfitMap.addMarker(latLng)
        Assert.assertEquals(latLng, marker.getPosition())

        // valid position
        val latLng2 = LatLng(42.693825, -63.998691)
        marker.setPosition(latLng2)
        Assert.assertEquals(latLng2, marker.getPosition())

        // invalid position
        val latLng3 = LatLng(412.693825, -653.998691)
        marker.setPosition(latLng3)
        Assert.assertEquals(latLng2, marker.getPosition())

    }


    @Test
    @UiThreadTest
    fun testAddRemoveMarker() {
        val marker = mapfitMap.addMarker(LatLng())
        org.junit.Assert.assertNotNull(marker)

        val removed = mapfitMap.removeMarker(marker)
        org.junit.Assert.assertTrue(removed)
    }

    @Test
    fun testAddMarkerWithAddress() {
        val expectedMarker = mapfitMap.addMarker(LatLng(40.74405, -73.99324))

        var actualMarker: Marker? = null

        mapfitMap.addMarker("119 w 24th st new york ny 10011", object : OnMarkerAddedCallback {
            override fun onMarkerAdded(marker: Marker) {
                actualMarker = marker

            }

            override fun onError(exception: Exception) {

            }
        })

        Thread.sleep(1500)
        Assert.assertEquals(expectedMarker.getPosition().lat, actualMarker?.getPosition()?.lat)
        Assert.assertEquals(expectedMarker.getPosition().lon, actualMarker?.getPosition()?.lon)
    }

}

