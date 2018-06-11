package com.mapfit.android.annotations

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.idling.CountingIdlingResource
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
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for [Polyline] functionality.
 *
 * Created by dogangulcan on 1/17/18.
 */
@RunWith(AndroidJUnit4::class)
class PolylineTest {

    @Mock
    private lateinit var polylineClickListener: OnPolylineClickListener

    private lateinit var mapfitMap: MapfitMap
    lateinit var mapView: MapView
    private var idlingResource = CountingIdlingResource("polyline_idling_resource")

    private val line by lazy {
        val list = mutableListOf<LatLng>().apply {
            add(LatLng(40.693825, -73.998691))
            add(LatLng(40.6902223, -73.9770368))
            add(LatLng(40.6930532, -73.9860919))
            add(LatLng(40.7061326, -74.000769))
        }
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

        mapView = activityRule.activity.findViewById(R.id.mapView)
        mapfitMap = mapView.getMap(MapTheme.MAPFIT_DAY.toString())
        mapfitMap.setOnPolylineClickListener(polylineClickListener)

        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    @UiThreadTest
    fun dispose() {
        Mapfit.dispose()
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    @UiThreadTest
    fun testStrokeColor() {
        val polyline = mapfitMap.addPolyline(PolylineOptions().points(line).strokeColor("#ffffff"))
        assertEquals("#ffffff", polyline.strokeColor)
    }

    @Test
    @UiThreadTest
    fun testStrokeWidth() {
        val polyline = mapfitMap.addPolyline(PolylineOptions().points(line).strokeWidth(50))
        assertEquals(50, polyline.strokeWidth)
    }

    @Test
    @UiThreadTest
    fun testStrokeOutlineWidth() {
        val polyline = mapfitMap.addPolyline(PolylineOptions().points(line).strokeOutlineWidth(50))
        assertEquals(50, polyline.strokeOutlineWidth)
    }

    @Test
    @UiThreadTest
    fun testCapType() {
        val polyline =
            mapfitMap.addPolyline(PolylineOptions().points(line).lineCapType(CapType.ROUND))
        assertEquals(CapType.ROUND, polyline.lineCapType)
    }

    @Test
    @UiThreadTest
    fun testJoinType() {
        val polyline =
            mapfitMap.addPolyline(PolylineOptions().points(line).lineJoinType(JoinType.ROUND))
        assertEquals(JoinType.ROUND, polyline.lineJoinType)
    }

    @Test
    @UiThreadTest
    fun testAddRemovePolyline() {
        val polyline = mapfitMap.addPolyline(PolylineOptions().points(line))

        assertNotNull(polyline)
        assertTrue(mapfitMap.has(polyline))

        mapfitMap.removePolyline(polyline)
        assertFalse(mapfitMap.has(polyline))

        val polyline2 = mapfitMap.addPolyline(PolylineOptions().points(line))
        polyline2.remove()
        assertFalse(mapfitMap.has(polyline2))
    }

    @Test
    @UiThreadTest
    fun testAddRemoveDifferentPolyline() {
        val polyline = mapfitMap.addPolyline(PolylineOptions().points(line))
        val polyline2 = mapfitMap.addPolyline(PolylineOptions().points(line))

        assertTrue(mapfitMap.has(polyline))
        assertTrue(mapfitMap.has(polyline2))

        mapfitMap.removePolyline(polyline)
        assertFalse(mapfitMap.has(polyline))
        assertTrue(mapfitMap.has(polyline2))
    }

    @Test
    @UiThreadTest
    fun testExtendingPolyline() {
        val polyline = mapfitMap.addPolyline(PolylineOptions().points(line))
        polyline.addPoints(line[1])
        polyline.addPoints(line[2])
        assertTrue(polyline.points.size > line.size)
    }

    @Test
    fun testPolylineColor() {
        val polyline = mapfitMap.addPolyline(
            PolylineOptions()
                .points(line)
                .strokeColor("#ff0000")
                .strokeWidth(5)
                .strokeOutlineColor("#0000ff")
                .strokeOutlineWidth(85)
                .lineCapType(CapType.ROUND)
        )

        mapfitMap.setLatLngBounds(polyline.getLatLngBounds(), 0.8f)
        mapfitMap.setZoom(19f)

        var tripleFirst = Triple(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
        var tripleSecond = Triple(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

        idlingResource.increment()

        mapView.getMapSnap(MapController.FrameCaptureCallback {
            val screenPosition = mapView.getScreenPosition(LatLng(40.6930532, -73.9860919))
            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            tripleFirst = Triple(Color.red(pixel), Color.green(pixel), Color.blue(pixel))

            val screenPosition2 = mapView.getScreenPosition(LatLng(40.692767, -73.978472))
            val pixel2 = it.getPixel(screenPosition2.x.toInt(), screenPosition2.y.toInt())
            tripleSecond = Triple(Color.red(pixel2), Color.green(pixel2), Color.blue(pixel2))

            idlingResource.decrement()
        })

        suspendViaGLSurface()
        assertEquals(255, tripleFirst.first)
        assertEquals(0, tripleFirst.second)
        assertEquals(0, tripleFirst.third)

        assertEquals(0, tripleSecond.first)
        assertEquals(0, tripleSecond.second)
        assertEquals(255, tripleSecond.third)
    }

    @Test
    fun testPolylineOrder() {
        val polyline = mapfitMap.addPolyline(
            PolylineOptions()
                .points(line)
                .strokeColor("#ff0000")
                .strokeWidth(15)
                .drawOrder(600)
        )

        val polyline2 = mapfitMap.addPolyline(
            PolylineOptions()
                .points(line)
                .strokeColor("#0000ff")
                .strokeWidth(15)
                .drawOrder(400)
        )

        val pixelCoordinate = LatLng(40.6930532, -73.9860919)

        mapfitMap.setLatLngBounds(polyline.getLatLngBounds(), 0.8f)
        mapfitMap.setZoom(19f)

        var triple = Triple(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

        idlingResource.increment()
        mapView.getMapSnap(MapController.FrameCaptureCallback {
            val screenPosition = mapView.getScreenPosition(pixelCoordinate)
            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            triple = Triple(Color.red(pixel), Color.green(pixel), Color.blue(pixel))
            idlingResource.decrement()
        })

        suspendViaGLSurface()
        assertEquals(255, triple.first)
        assertEquals(0, triple.second)
        assertEquals(0, triple.third)

        polyline.drawOrder = 400
        polyline2.drawOrder = 600

        idlingResource.increment()
        mapView.getMapSnap(MapController.FrameCaptureCallback {
            val screenPosition = mapView.getScreenPosition(pixelCoordinate)
            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            triple = Triple(Color.red(pixel), Color.green(pixel), Color.blue(pixel))
            idlingResource.decrement()
        })

        suspendViaGLSurface()
        assertEquals(0, triple.first)
        assertEquals(0, triple.second)
        assertEquals(255, triple.third)

        polyline.drawOrder = 800

        idlingResource.increment()
        mapView.getMapSnap(MapController.FrameCaptureCallback {
            val screenPosition = mapView.getScreenPosition(pixelCoordinate)
            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            triple = Triple(Color.red(pixel), Color.green(pixel), Color.blue(pixel))
            idlingResource.decrement()
        })

        suspendViaGLSurface()
        assertEquals(255, triple.first)
        assertEquals(0, triple.second)
        assertEquals(0, triple.third)
    }

    @Test
    fun testPolylineClickListener() {
        val polyline = mapfitMap.addPolyline(PolylineOptions().points(line))

        mapfitMap.apply {
            setCenter(polyline.points.first())
            setZoom(14f)
        }

        clickOnPolyline(polyline)

        Mockito.verify(polylineClickListener).onPolylineClicked(polyline)
    }

    @Test
    fun testPolylineObject() {
        val polyline = mapfitMap.addPolyline(
            PolylineOptions()
                .points(line)
                .tag(5)
        )

        mapfitMap.apply {
            setCenter(polyline.points.first())
            setZoom(14f)
        }

        val captor = ArgumentCaptor.forClass(Polyline::class.java)

        clickOnPolyline(polyline)

        Mockito.verify(polylineClickListener).onPolylineClicked(capture(captor) ?: polyline)

        assertEquals(5, captor.value.tag)
    }

    @Test
    fun testCustomYamlLayer() {
        mapfitMap.setOnMapThemeLoadListener(object : OnMapThemeLoadListener {
            override fun onLoaded() {
                idlingResource.decrement()
            }

            override fun onError() {
                idlingResource.decrement()
            }
        })
        // mapfit-custom-test yaml has an import yaml inside and that causes onLoaded to be
        // called twice
        idlingResource.increment()
        idlingResource.increment()
        mapfitMap.getMapOptions().customTheme = "mapfit-custom-test.yaml"

        suspendViaGLSurface()

        val polyline = mapfitMap.addPolyline(
            PolylineOptions()
                .points(line)
                .layerName("my_custom_line")
        )

        mapfitMap.setLatLngBounds(polyline.getLatLngBounds(), 0.8f)
        mapfitMap.setZoom(19f)

        var triple = Triple(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

        idlingResource.increment()
        mapView.getMapSnap(MapController.FrameCaptureCallback {
            val screenPosition = mapView.getScreenPosition(LatLng(40.6930532, -73.9860919))
            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            triple = Triple(Color.red(pixel), Color.green(pixel), Color.blue(pixel))
            idlingResource.decrement()
        })

        suspendViaGLSurface()
        assertEquals(255, triple.first)
        assertEquals(0, triple.second)
        assertEquals(0, triple.third)
    }

    private fun clickOnPolyline(polyline: Polyline) = runBlocking {
        delay(500)

        val screenPosition =
            polyline.mapBindings.keys.first()
                .latLngToScreenPosition(polyline.points[2])

        Espresso.onView(ViewMatchers.withId(R.id.glSurface))
            .perform(clickOn(screenPosition.x.toInt(), screenPosition.y.toInt()))

        delay(1500)
    }

}

