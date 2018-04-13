package com.mapfit.android.annotations

import android.content.Context
import android.graphics.Color
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.*
import com.mapfit.android.annotations.callback.OnPolygonClickListener
import com.mapfit.android.geometry.LatLng
import kotlinx.coroutines.experimental.android.UI
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

    lateinit var mapView: MapView

    private val poly by lazy {
        val list = mutableListOf<List<LatLng>>()
        val subList = mutableListOf<LatLng>()

        subList.add(LatLng(40.746046, -74.005882))
        subList.add(LatLng(40.736589, -73.977800))
        subList.add(LatLng(40.708213, -74.012489))
        subList.add(LatLng(40.746046, -74.005882))
        list.add(subList)
        list
    }

    @Rule
    @JvmField
    val activityRule: ActivityTestRule<MapViewTestActivity> = ActivityTestRule(
        MapViewTestActivity::class.java,
        true,
        true
    )

    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        Mapfit.getInstance(mMockContext, mMockContext.getString(R.string.mapfit_debug_api_key))
        mapView = activityRule.activity.findViewById(R.id.mapView)
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
        val polygon = mapfitMap.addPolygon(poly)

        assertNotNull(polygon)
        assertTrue(mapfitMap.has(polygon))
        assertEquals(1, polygon.rings.size)

        mapfitMap.removePolygon(polygon)
        assertFalse(mapfitMap.has(polygon))
    }

    @Test
    fun testPolygonFillColor() = runBlocking(UI) {
        val polygon = mapfitMap.addPolygon(poly)
        polygon.polygonOptions.fillColor = "#ff0000"

        mapfitMap.setLatLngBounds(polygon.getLatLngBounds(), 0.5f)
        mapfitMap.setZoom(19f)

        var redValue = 0
        var blueValue = 0
        var greenValue = 0

        mapView.getMapSnap {
            val screenPosition =
                polygon.mapBindings.keys.first()
                    .lngLatToScreenPosition(LatLng(40.734839, -73.994748))
            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            redValue = Color.red(pixel)
            blueValue = Color.blue(pixel)
            greenValue = Color.green(pixel)
        }

        delay(2000)
        assertEquals(255, redValue)
        assertEquals(0, blueValue)
        assertEquals(0, greenValue)
    }


    @Test
    fun testPolygonOrder() = runBlocking(UI) {
        val polygon = mapfitMap.addPolygon(poly)
        polygon.polygonOptions.fillColor = "#ff0000"
        polygon.polygonOptions.strokeWidth = 15

        val polygon2 = mapfitMap.addPolygon(poly)
        polygon2.polygonOptions.fillColor = "#0000ff"
        polygon2.polygonOptions.strokeWidth = 15

        val pixelCoordinate = LatLng(40.741596, -73.994686)

        mapfitMap.setLatLngBounds(polygon.getLatLngBounds(), 0.8f)
        mapfitMap.setZoom(19f)

        polygon.polygonOptions.drawOrder = 600
        polygon2.polygonOptions.drawOrder = 400

        var triple = Triple(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

        mapView.getMapSnap {
            val screenPosition = mapView.getScreenPosition(pixelCoordinate)
            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            triple = Triple(Color.red(pixel), Color.green(pixel), Color.blue(pixel))
        }

        delay(2000)
        assertEquals(255, triple.first)
        assertEquals(0, triple.second)
        assertEquals(0, triple.third)

        polygon.polygonOptions.drawOrder = 400
        polygon2.polygonOptions.drawOrder = 600

        delay(500)
        mapView.getMapSnap {
            val screenPosition = mapView.getScreenPosition(pixelCoordinate)
            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            triple = Triple(Color.red(pixel), Color.green(pixel), Color.blue(pixel))
        }

        delay(500)
        assertEquals(0, triple.first)
        assertEquals(0, triple.second)
        assertEquals(255, triple.third)

        polygon.polygonOptions.drawOrder = 800

        delay(500)
        mapView.getMapSnap {
            val screenPosition = mapView.getScreenPosition(pixelCoordinate)
            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            triple = Triple(Color.red(pixel), Color.green(pixel), Color.blue(pixel))
        }

        delay(500)
        assertEquals(255, triple.first)
        assertEquals(0, triple.second)
        assertEquals(0, triple.third)
    }

    @Test
    fun testPolygonClickListener() = runBlocking {
        delay(400)
        mapfitMap.setCenter(LatLng(40.741596, -73.994686))
        mapfitMap.setZoom(14f)
        mapfitMap.setOnPolygonClickListener(polygonClickListener)

        val polygon = mapfitMap.addPolygon(poly)

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
                .lngLatToScreenPosition(LatLng(40.741596, -73.994686))

        Espresso.onView(ViewMatchers.withId(R.id.glSurface))
            .perform(clickOn(screenPosition.x.toInt(), screenPosition.y.toInt()))

        Thread.sleep(1500)
    }


}

