package com.mapfit.android.map

import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.*
import com.mapfit.android.MapOptions.Companion.MAP_MAX_ZOOM
import com.mapfit.android.MapOptions.Companion.MAP_MIN_ZOOM
import com.mapfit.android.geometry.LatLng
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for [MapView] functionality.
 *
 * Created by dogangulcan on 1/8/18.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MapViewTest {

    private lateinit var mapView: MapView
    private lateinit var mapfitMap: MapfitMap
    private val latLng = LatLng(40.7441855, -73.995394)

    @Rule
    @JvmField
    val activityRule: ActivityTestRule<MapViewTestActivity> = ActivityTestRule(
        MapViewTestActivity::class.java,
        true,
        true
    )

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        mapView = activityRule.activity.findViewById(R.id.mapView)
        mapfitMap = mapView.getMap(MapTheme.MAPFIT_DAY.toString())
    }

    @After
    @UiThreadTest
    fun cleanup() {
        Mapfit.dispose()
    }

    @Test
    @UiThreadTest
    fun testInitValuesExistence() {
        Assert.assertNotNull(mapView)
        Assert.assertNotNull(mapfitMap)
    }

    @Test
    @UiThreadTest
    fun testZoom() {
        val zoomLevel = 5f
        mapfitMap.setZoom(zoomLevel)
        Assert.assertEquals("Setting map zoom", zoomLevel, mapfitMap.getZoom())
    }

    @Test
    @UiThreadTest
    fun testOutOfBoundZoom() {
        val zoomLevel = 5f
        mapfitMap.setZoom(zoomLevel)

        val zoomLevelAboveMax = (MAP_MAX_ZOOM + 5).toFloat()
        mapfitMap.setZoom(zoomLevelAboveMax)

        // zoomLevelAboveMax should be normalized to max
        Assert.assertEquals(MAP_MAX_ZOOM.toFloat(), mapfitMap.getZoom())

        val zoomLevelBelowMin = (MAP_MIN_ZOOM - 5).toFloat()
        mapfitMap.setZoom(zoomLevelBelowMin)

        // zoomLevelBelowMin should be normalized to min
        Assert.assertEquals(MAP_MIN_ZOOM.toFloat(), mapfitMap.getZoom(), 1f)
    }

    @Test
    @UiThreadTest
    fun testSetCenter() {
        mapfitMap.setCenter(latLng)

        val actualLatLng = mapfitMap.getCenter()
        Assert.assertEquals(latLng.lat, actualLatLng?.lat ?: 0.0, 0.0000001)
        Assert.assertEquals(latLng.lng, actualLatLng?.lng ?: 0.0, 0.0000001)
    }

    @Test
    @UiThreadTest
    fun testSetCenterWithOffset() {
        mapfitMap.setZoom(14f)
        mapfitMap.setCenterWithOffset(latLng, 300f, 300f)

        val expectedCenter = LatLng(40.738608074618185, -73.9880383014679)
        val actualCenter = mapfitMap.getCenter() ?: LatLng()

        Assert.assertEquals(expectedCenter.lat, actualCenter.lat, 0.005)
        Assert.assertEquals(expectedCenter.lng, actualCenter.lng, 0.005)
    }

    @Test
    @UiThreadTest
    fun testSetTilt() {
        val tilt = 64f
        mapfitMap.setTilt(tilt)
        Assert.assertEquals(tilt, mapfitMap.getTilt())
    }

    @Test
    @UiThreadTest
    fun testSetRotation() {
        val rotation = 2f
        mapfitMap.setRotation(rotation)
        Assert.assertEquals(rotation, mapfitMap.getRotation())
    }

    @Test
    @UiThreadTest
    fun testAddRemoveLayers() {
        val layer = Layer()
        mapfitMap.addLayer(layer)
        Assert.assertEquals(layer, mapfitMap.getLayers().last())

        mapfitMap.removeLayer(layer)
        Assert.assertTrue(mapfitMap.getLayers().isEmpty())
    }

    @Test
    @UiThreadTest
    fun testScreenPositionToLatLng() {
        mapfitMap.setCenter(latLng)
        val screenPosition = mapfitMap.latLngToScreenPosition(latLng)
        val coordinates = mapfitMap.screenPositionToLatLng(screenPosition)

        Assert.assertEquals(latLng.lat, coordinates.lat, 0.0001)
        Assert.assertEquals(latLng.lng, coordinates.lng, 0.0001)
    }

}
