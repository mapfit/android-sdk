package com.mapfit.android.annotations

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.*
import com.mapfit.android.geometry.LatLng
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
class PolylineTest {

    private val mMockContext: Context = InstrumentationRegistry.getContext()

    private lateinit var mapfitMap: MapfitMap

    private val line by lazy {
        val list = mutableListOf<LatLng>()

        list.add(LatLng(40.693825, -73.998691))
        list.add(LatLng(40.6902223, -73.9770368))
        list.add(LatLng(40.6930532, -73.9860919))
        list.add(LatLng(40.7061326, -74.000769))
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
                this@PolylineTest.mapfitMap = mapfitMap
            }
        })
    }

    @Test
    @UiThreadTest
    fun testAddRemovePolyline() {
        val polyline = mapfitMap.addPolyline(line)

        assertNotNull(polyline)
        assertTrue(mapfitMap.has(polyline))
//        assertEquals(4, polyline.points.size)

        mapfitMap.removePolyline(polyline)
        assertFalse(mapfitMap.has(polyline))
    }


}

