package com.mapfit.android

import android.location.Location
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.mapfit.android.MapOptions.Companion.MAP_MAX_ZOOM
import com.mapfit.android.MapOptions.Companion.MAP_MIN_ZOOM
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.location.LocationListener
import com.mapfit.android.location.LocationPriority
import kotlinx.android.synthetic.main.mf_overlay_map_controls.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for [MapView] and [MapOptions] functionality.
 *
 * Created by dogangulcan on 1/8/18.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MapViewTest {

    private lateinit var mapView: com.mapfit.android.MapView

    private lateinit var mapfitMap: MapfitMap

    @Mock
    private lateinit var onMapClickListener: OnMapClickListener

    @Mock
    private lateinit var onMapDoubleClickListener: OnMapDoubleClickListener

    @Mock
    private lateinit var onMapLongClickListener: OnMapLongClickListener

    @Mock
    private lateinit var onMapPanListener: OnMapPanListener

    @Mock
    private lateinit var onMapPinchListener: OnMapPinchListener

    @Mock
    private lateinit var locationListener: LocationListener

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

    private lateinit var idlingResource: CountingIdlingResource

    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        idlingResource = activityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)

        idlingResource.registerIdleTransitionCallback({
            mapfitMap = activityRule.activity.mapfitMap
            mapView = activityRule.activity.mapView
        })

        activityRule.activity.init()

    }

    @After
    fun cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    @UiThreadTest
    fun testInitValuesExistence() {
        Assert.assertNotNull(mapView)
        Assert.assertNotNull(mapfitMap)
    }

    @Test
    @UiThreadTest
    fun testDefaultValues() {
        Assert.assertEquals(MapTheme.MAPFIT_DAY, mapfitMap.getMapOptions().theme)
        Assert.assertEquals(View.GONE, mapView.zoomControlsView.visibility)
        Assert.assertEquals(View.GONE, mapView.btnRecenter.visibility)
        Assert.assertEquals(View.GONE, mapView.btnCompass.visibility)
        Assert.assertEquals(View.VISIBLE, mapView.getAttributionImage().visibility)
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
    fun testSettingCenter() {
        val latLng = LatLng(40.7441855, -73.995394)
        mapfitMap.setCenter(latLng)

        val actualLatLng = mapfitMap.getCenter()
        Assert.assertEquals(latLng.lat, actualLatLng.lat, 0.0000001)
        Assert.assertEquals(latLng.lng, actualLatLng.lng, 0.0000001)

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
    fun testStyleChanges() {
        mapfitMap.getMapOptions().theme = MapTheme.MAPFIT_NIGHT
        Assert.assertEquals(MapTheme.MAPFIT_NIGHT, mapfitMap.getMapOptions().theme)
    }

    @Test
    @UiThreadTest
    fun testZoomControlVisibility() {
        mapfitMap.getMapOptions().zoomControlsEnabled = true
        Assert.assertEquals(View.VISIBLE, mapView.zoomControlsView.visibility)

        mapfitMap.getMapOptions().zoomControlsEnabled = false
        Assert.assertEquals(View.GONE, mapView.zoomControlsView.visibility)
    }

    @Test
    @UiThreadTest
    fun testCompassVisibility() {
        mapfitMap.getMapOptions().compassButtonEnabled = true
        Assert.assertEquals(View.VISIBLE, mapView.btnCompass.visibility)

        mapfitMap.getMapOptions().compassButtonEnabled = false
        Assert.assertEquals(View.GONE, mapView.btnCompass.visibility)
    }

    @Test
    @UiThreadTest
    fun testRecenterVisibility() {
        mapfitMap.getMapOptions().recenterButtonEnabled = true
        Assert.assertEquals(View.VISIBLE, mapView.btnRecenter.visibility)

        mapfitMap.getMapOptions().recenterButtonEnabled = false
        Assert.assertEquals(View.GONE, mapView.btnRecenter.visibility)
    }

    @Test
    fun testMapClickListener() {
        runBlocking {
            delay(400)
            mapfitMap.setOnMapClickListener(onMapClickListener)

            onView(withId(R.id.glSurface)).perform(click())
            delay(600)

            verify(onMapClickListener, times(1))
                .onMapClicked(Mockito.any(LatLng::class.java) ?: LatLng())
        }
    }


    @Test
    fun testOnMapDoubleClickListener() {
        runBlocking {
            delay(400)
            mapfitMap.setOnMapDoubleClickListener(onMapDoubleClickListener)

            onView(withId(R.id.glSurface)).perform(doubleClick())
            delay(600)

            verify(onMapDoubleClickListener, times(1))
                .onMapDoubleClicked(Mockito.any(LatLng::class.java) ?: LatLng())
        }
    }

    @Test
    fun testOnMapLongClickListener() {
        runBlocking {
            delay(400)
            mapfitMap.setOnMapLongClickListener(onMapLongClickListener)

            onView(withId(R.id.glSurface)).perform(longClick())
            delay(600)

            verify(onMapLongClickListener, times(1))
                .onMapLongClicked(Mockito.any(LatLng::class.java) ?: LatLng())
        }
    }


    /**
     * Location update is not mocked.
     */
    @Test
    fun testOnUserLocationListener() {
        runBlocking(UI) {
            delay(400)
            mapfitMap.getMapOptions().setUserLocationEnabled(
                true,
                LocationPriority.HIGH_ACCURACY,
                locationListener
            )

            delay(20000)

            verify(locationListener, atLeastOnce())
                .onLocation(Mockito.any(Location::class.java) ?: Location(""))

            mapfitMap.getMapOptions().setUserLocationEnabled(false)
        }
    }


    @Test
    fun testOnMapPanListener() {
        runBlocking {
            delay(400)
            mapfitMap.setOnMapPanListener(onMapPanListener)

            onView(withId(R.id.glSurface)).perform(swipeDown())
            delay(600)

            verify(onMapPanListener, atLeastOnce()).onMapPan()
        }
    }

    @Test
    fun testOnMapPinchListener() {

        runBlocking {
            delay(400)
            mapfitMap.setOnMapPinchListener(onMapPinchListener)

            onView(withId(R.id.glSurface)).perform(pinchIn())
            delay(600)

            verify(onMapPinchListener, atLeastOnce()).onMapPinch()

            onView(withId(R.id.glSurface)).perform(pinchOut())
            delay(600)

            verify(onMapPinchListener, atLeastOnce()).onMapPinch()
        }
    }


}
