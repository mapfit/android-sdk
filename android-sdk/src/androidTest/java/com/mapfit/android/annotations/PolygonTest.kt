package com.mapfit.android.annotations

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.*
import com.mapfit.android.annotations.callback.OnPolygonClickListener
import com.mapfit.android.geometry.LatLng
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for [Marker] functionality.
 *
 * Created by dogangulcan on 1/17/18.
 */
@RunWith(AndroidJUnit4::class)
class PolygonTest {

    private val mMockContext: Context = InstrumentationRegistry.getContext()

    @Mock
    private lateinit var polygonClickListener: OnPolygonClickListener

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

        Mapfit.getInstance(mMockContext, mMockContext.getString(R.string.mapfit_debug_api_key))
        val mapView: MapView = activityRule.activity.findViewById(R.id.mapView)
        mapView.getMapAsync(onMapReadyCallback = object : OnMapReadyCallback {
            override fun onMapReady(mapfitMap: MapfitMap) {
                this@PolygonTest.mapfitMap = mapfitMap
            }
        })
    }

    @After
    @UiThreadTest
    fun dispose() {
        Mapfit.dispose()
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

    @Test
    fun testPolylineClickListener() = runBlocking {
        delay(400)
        mapfitMap.setCenter(line.first().first())
        mapfitMap.setZoom(17f)
        mapfitMap.setOnPolygonClickListener(polygonClickListener)

        val polygon = mapfitMap.addPolygon(line)

        clickPolygon(polygon)

        Mockito.verify(
            polygonClickListener,
            Mockito.times(1)
        ).onPolygonClicked(polygon)
    }

    private fun clickPolygon(polygon: Polygon) {
        Thread.sleep(500)

        val screenPosition =
            polygon.mapBindings.keys.first()
                .lngLatToScreenPosition(polygon.points.first().first())

        Espresso.onView(ViewMatchers.withId(R.id.glSurface))
            .perform(clickOn(screenPosition.x.toInt(), screenPosition.y.toInt()))

        Thread.sleep(1500)
    }


}

