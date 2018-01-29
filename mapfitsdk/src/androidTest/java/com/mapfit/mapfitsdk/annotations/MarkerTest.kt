package com.mapfit.mapfitsdk.annotations

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.mapfitsdk.*
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerAddedCallback
import com.mapfit.mapfitsdk.annotations.callback.OnMarkerClickListener
import com.mapfit.mapfitsdk.geometry.LatLng
import junit.framework.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
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

    @Mock
    private lateinit var onMarkerClickListener: OnMarkerClickListener

    @Rule
    @JvmField
    val activityRule: ActivityTestRule<DummyActivity> = ActivityTestRule(
        DummyActivity::class.java,
        true,
        true
    )

    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        Mapfit.getInstance(mMockContext, "591dccc4e499ca0001a4c6a41a2ed1be54804856508265221862231b")
//        val mapView = MapView(mMockContext)
        val mapView: MapView = activityRule.activity.findViewById(R.id.mapView)

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
        Assert.assertNotNull(marker)

        val removed = mapfitMap.removeMarker(marker)
        Assert.assertTrue(removed)
    }

    @Test
    fun testAddMarkerWithAddress() {
        Thread.sleep(500)

        val expectedMarker = mapfitMap.addMarker(LatLng(40.74405, -73.99324))

        var actualMarker: Marker? = null

        mapfitMap.addMarker("119 w 24th st new york ny 10011", object : OnMarkerAddedCallback {
            override fun onMarkerAdded(marker: Marker) {
                actualMarker = marker
            }

            override fun onError(exception: Exception) {

            }
        })

        Thread.sleep(500)
        Assert.assertEquals(
            expectedMarker.getPosition().lat,
            actualMarker?.getPosition()?.lat
                    ?: 0.0,
            0.0001
        )
        Assert.assertEquals(
            expectedMarker.getPosition().lon,
            actualMarker?.getPosition()?.lon
                    ?: 0.0,
            0.0001
        )
    }

    @Test
    fun testMapClickListener() {
        Thread.sleep(500)

        val latLng = LatLng(40.693825, -73.998691)
        mapfitMap.setCenter(latLng)
        Thread.sleep(500)

        mapfitMap.setOnMarkerClickListener(onMarkerClickListener)
        val marker = mapfitMap.addMarker(latLng)
        val screenPosition = marker.getScreenPosition()

        Espresso.onView(ViewMatchers.withId(R.id.glSurface))
            .perform(clickOn(screenPosition.x.toInt(), screenPosition.y.toInt()))

        Thread.sleep(1500)
        verify(onMarkerClickListener, times(1)).onMarkerClicked(marker)


    }

}

