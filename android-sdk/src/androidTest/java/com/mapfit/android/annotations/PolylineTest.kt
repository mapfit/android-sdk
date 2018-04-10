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
import com.mapfit.android.annotations.callback.OnPolylineClickListener
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
class PolylineTest {

    private val mMockContext: Context = InstrumentationRegistry.getContext()

    @Mock
    private lateinit var polylineClickListener: OnPolylineClickListener

    private lateinit var mapfitMap: MapfitMap

    lateinit var mapView: MapView

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
                this@PolylineTest.mapfitMap = mapfitMap
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
    fun testAddRemovePolyline() {
        val polyline = mapfitMap.addPolyline(line)

        assertNotNull(polyline)
        assertTrue(mapfitMap.has(polyline))

        mapfitMap.removePolyline(polyline)
        assertFalse(mapfitMap.has(polyline))

        val polyline2 = mapfitMap.addPolyline(line)
        polyline2.remove()
        assertFalse(mapfitMap.has(polyline2))
    }

    @Test
    @UiThreadTest
    fun testAddRemoveDifferentPolyline() {
        val polyline = mapfitMap.addPolyline(line)
        val polyline2 = mapfitMap.addPolyline(line)

        assertTrue(mapfitMap.has(polyline))
        assertTrue(mapfitMap.has(polyline2))

        mapfitMap.removePolyline(polyline)
        assertFalse(mapfitMap.has(polyline))
        assertTrue(mapfitMap.has(polyline2))
    }

    @Test
    @UiThreadTest
    fun testExtendingPolyline() {
        val polyline = mapfitMap.addPolyline(line)
        polyline.addPoints(line[1])
        polyline.addPoints(line[2])
        assertTrue(polyline.points.size > line.size)
    }

    @Test
    fun testPolylineColor() = runBlocking(UI) {
        val polyline = mapfitMap.addPolyline(line)
        polyline.polylineOptions.strokeColor = "#ff0000"
        polyline.polylineOptions.strokeWidth = 5

        polyline.polylineOptions.strokeOutlineColor = "#0000ff"
        polyline.polylineOptions.strokeOutlineWidth = 85
        polyline.polylineOptions.lineCapType = CapType.ROUND


        mapfitMap.setLatLngBounds(polyline.getLatLngBounds(), 0.8f)
        mapfitMap.setZoom(19f)

        var redValue = 0
        var blueValue = 0
        var greenValue = 0

        var outlineRedValue = 0
        var outlineBlueValue = 0
        var outlineGreenValue = 0

        mapView.getMapSnap {
            val screenPosition =
                polyline.mapBindings.keys.first()
                    .lngLatToScreenPosition(LatLng(40.6930532, -73.9860919))

            val screenPositionOutline =
                polyline.mapBindings.keys.first()
                    .lngLatToScreenPosition(LatLng(40.692767, -73.978472))

            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            val pixelOutline =
                it.getPixel(screenPositionOutline.x.toInt(), screenPositionOutline.y.toInt())

            redValue = Color.red(pixel)
            blueValue = Color.blue(pixel)
            greenValue = Color.green(pixel)

            outlineRedValue = Color.red(pixelOutline)
            outlineBlueValue = Color.blue(pixelOutline)
            outlineGreenValue = Color.green(pixelOutline)
        }

        delay(1000)
        assertEquals(255, redValue)
        assertEquals(0, blueValue)
        assertEquals(0, greenValue)

        assertEquals(0, outlineRedValue)
        assertEquals(255, outlineBlueValue)
        assertEquals(0, outlineGreenValue)
    }

    @Test
    fun testPolylineClickListener() = runBlocking {
        delay(400)
        mapfitMap.setCenter(line.first())
        mapfitMap.setZoom(17f)
        mapfitMap.setOnPolylineClickListener(polylineClickListener)

        val polyline = mapfitMap.addPolyline(line)

        clickOnPolyline(polyline)

        Mockito.verify(
            polylineClickListener,
            Mockito.times(1)
        ).onPolylineClicked(polyline)
    }

    private fun clickOnPolyline(polyline: Polyline) {
        Thread.sleep(500)

        val screenPosition =
            polyline.mapBindings.keys.first()
                .lngLatToScreenPosition(polyline.points.first())

        Espresso.onView(ViewMatchers.withId(R.id.glSurface))
            .perform(clickOn(screenPosition.x.toInt(), screenPosition.y.toInt()))

        Thread.sleep(1500)
    }

}

