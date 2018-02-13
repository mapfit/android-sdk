package com.mapfit.mapfitsdk.annotations

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.mapfitsdk.*
import com.mapfit.mapfitsdk.geometry.LatLng
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for [Marker] functionality.
 *
 * Created by dogangulcan on 1/17/18.
 */
@RunWith(AndroidJUnit4::class)
class PolygonTest {

    private val mMockContext: Context = InstrumentationRegistry.getContext()

    private lateinit var mapfitMap: MapfitMap

    private val line by lazy {
        val list = mutableListOf<List<LatLng>>()
        val subList = mutableListOf<LatLng>()

        subList.add(LatLng(40.693825, -73.998691))
        subList.add(LatLng(40.6902223, -73.9770368))
        subList.add(LatLng(40.6930532, -73.9860919))
        subList.add(LatLng(40.7061326, -74.000769))
        subList.add(LatLng(40.693825, -73.998691))
        list.add(subList)
        list
    }

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

        Mapfit.getInstance(mMockContext, mMockContext.getString(R.string.api_key))
        val mapView: MapView = activityRule.activity.findViewById(R.id.mapView)
        mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@PolygonTest.mapfitMap = mapfitMap
            }
        })
    }

    @Test
    @UiThreadTest
    fun testAddRemovePolygon() {
        val polygon = mapfitMap.addPolygon(line)

        assertNotNull(polygon)
        assertTrue(mapfitMap.has(polygon))
        assertEquals(1, polygon.rings.size)

        mapfitMap.removePolygon(polygon)
        assertFalse(mapfitMap.has(polygon))
    }


}

