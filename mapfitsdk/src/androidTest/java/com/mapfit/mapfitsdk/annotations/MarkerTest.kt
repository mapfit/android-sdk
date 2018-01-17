package com.mapfit.mapfitsdk.annotations

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import com.mapfit.mapfitsdk.MapView
import com.mapfit.mapfitsdk.MapfitMap
import com.mapfit.mapfitsdk.OnMapReadyCallback
import com.mapfit.mapfitsdk.geometry.LatLng
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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

    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        val mapView = MapView(mMockContext, null)

        mapView.getMapAsync(object : OnMapReadyCallback {
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


}