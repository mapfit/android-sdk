package com.mapfit.android.annotations

import android.graphics.Color
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.idling.CountingIdlingResource
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
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for [Polygon] functionality.
 *
 * Created by dogangulcan on 1/17/18.
 */
@RunWith(AndroidJUnit4::class)
class PolygonTest {

    @Mock
    private lateinit var polygonClickListener: OnPolygonClickListener

    private lateinit var mapView: MapView
    private lateinit var mapfitMap: MapfitMap
    private var idlingResource = CountingIdlingResource("polygon_idling_resource")

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

        mapView = activityRule.activity.findViewById(R.id.mapView)
        mapfitMap = mapView.getMap(MapTheme.MAPFIT_DAY.toString())
        mapfitMap.setOnPolygonClickListener(polygonClickListener)

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
    fun testSetFillColor() {
        val polygon = mapfitMap.addPolygon(PolygonOptions().points(poly).fillColor("#000000"))
        assertEquals("#000000", polygon.fillColor)
    }

    @Test
    @UiThreadTest
    fun testSetStrokeColor() {
        val polygon = mapfitMap.addPolygon(PolygonOptions().points(poly).strokeColor("#ffffff"))
        assertEquals("#ffffff", polygon.strokeColor)
    }

    @Test
    @UiThreadTest
    fun testSetStrokeWidth() {
        val polygon = mapfitMap.addPolygon(PolygonOptions().points(poly).strokeWidth(50))
        assertEquals(50, polygon.strokeWidth)
    }

    @Test
    @UiThreadTest
    fun testSetStrokeOutlineWidth() {
        val polygon = mapfitMap.addPolygon(PolygonOptions().points(poly).strokeOutlineWidth(50))
        assertEquals(50, polygon.strokeOutlineWidth)
    }

    @Test
    @UiThreadTest
    fun testSetJoinType() {
        val polygon =
            mapfitMap.addPolygon(PolygonOptions().points(poly).lineJoinType(JoinType.ROUND))
        assertEquals(JoinType.ROUND, polygon.lineJoinType)
    }

    @Test
    @UiThreadTest
    fun testAddRemovePolygon() {
        val polygon = mapfitMap.addPolygon(PolygonOptions().points(poly))

        assertNotNull(polygon)
        assertTrue(mapfitMap.has(polygon))
        assertEquals(1, polygon.rings.size)

        mapfitMap.removePolygon(polygon)
        assertFalse(mapfitMap.has(polygon))
    }

    @Test
    fun testPolygonFillColor() {
        val polygon = mapfitMap.addPolygon(
            PolygonOptions()
                .points(poly)
                .fillColor("#ff0000")
        )

        mapfitMap.setLatLngBounds(polygon.getLatLngBounds())

        var redValue = Int.MIN_VALUE
        var blueValue = Int.MIN_VALUE
        var greenValue = Int.MIN_VALUE

        idlingResource.increment()
        mapView.getMapSnap(MapController.FrameCaptureCallback {
            val screenPosition =
                polygon.mapBindings.keys.first()
                    .latLngToScreenPosition(LatLng(40.734839, -73.994748))
            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            redValue = Color.red(pixel)
            blueValue = Color.blue(pixel)
            greenValue = Color.green(pixel)
            idlingResource.decrement()
        })

        suspendViaGLSurface()
        assertEquals(255, redValue)
        assertEquals(0, blueValue)
        assertEquals(0, greenValue)
    }

    @Test
    fun testPolygonOrder() {
        val polygon =
            mapfitMap.addPolygon(
                PolygonOptions()
                    .points(poly)
                    .fillColor("#ff0000")
                    .drawOrder(600)
            )

        val polygon2 = mapfitMap.addPolygon(
            PolygonOptions()
                .points(poly)
                .fillColor("#0000ff")
                .drawOrder(400)
        )

        val pixelCoordinate = LatLng(40.741596, -73.994686)

        mapfitMap.setLatLngBounds(polygon.getLatLngBounds(), 0.5f)

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

        polygon.drawOrder = 400
        polygon2.drawOrder = 600

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

        polygon.drawOrder = 800

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

        val polyline = mapfitMap.addPolygon(
            PolygonOptions()
                .points(poly)
                .layerName("my_custom_polygon")
        )

        mapfitMap.setLatLngBounds(polyline.getLatLngBounds(), 0.8f)
        mapfitMap.setZoom(19f)

        var triple = Triple(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

        idlingResource.increment()
        mapView.getMapSnap(MapController.FrameCaptureCallback {
            val screenPosition = mapView.getScreenPosition(LatLng(40.741596, -73.994686))
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
    fun testPolygonClickListener() {
        mapfitMap.apply {
            setCenter(LatLng(40.741596, -73.994686))
            setZoom(14f)
        }

        val polygon = mapfitMap.addPolygon(PolygonOptions().points(poly))

        clickPolygon(polygon)

        Mockito.verify(polygonClickListener).onPolygonClicked(polygon)
    }

    @Test
    fun testPolygonObject() {
        mapfitMap.apply {
            setCenter(LatLng(40.741596, -73.994686))
            setZoom(14f)
        }

        val polygon = mapfitMap.addPolygon(
            PolygonOptions()
                .points(poly)
                .data(5)
        )

        val captor = ArgumentCaptor.forClass(Polygon::class.java)

        clickPolygon(polygon)

        Mockito.verify(polygonClickListener).onPolygonClicked(capture(captor) ?: polygon)

        assertEquals(5, captor.value.data)
    }

    private fun clickPolygon(polygon: Polygon) = runBlocking {
        delay(200)
        val screenPosition =
            polygon.mapBindings.keys.first()
                .latLngToScreenPosition(LatLng(40.741596, -73.994686))

        Espresso.onView(ViewMatchers.withId(R.id.glSurface))
            .perform(clickOn(screenPosition.x.toInt(), screenPosition.y.toInt()))

        delay(1500)
    }


}

