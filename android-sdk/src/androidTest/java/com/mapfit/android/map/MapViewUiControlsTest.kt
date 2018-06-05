package com.mapfit.android.map

import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.mapfit.android.MapView
import com.mapfit.android.MapViewTestActivity
import com.mapfit.android.MapfitMap
import kotlinx.android.synthetic.main.mf_overlay_map_controls.view.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for UI controls.
 *
 * Created by dogangulcan on 1/8/18.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MapViewUiControlsTest {

    private lateinit var mapView: MapView

    private lateinit var mapfitMap: MapfitMap

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

        activityRule.activity.initWithCustomYaml()
    }

    @After
    fun cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    @UiThreadTest
    fun testDefaultValues() {
        Assert.assertEquals(View.GONE, mapView.zoomControlsView.visibility)
        Assert.assertEquals(View.GONE, mapView.btnRecenter.visibility)
        Assert.assertEquals(View.GONE, mapView.btnCompass.visibility)
        Assert.assertEquals(View.GONE, mapView.btnUserLocation.visibility)
        Assert.assertEquals(View.VISIBLE, mapView.getAttributionImage().visibility)
    }

    @Test
    @UiThreadTest
    fun testAttribution() {
        Assert.assertEquals(View.VISIBLE, mapView.getAttributionImage().visibility)
    }

    @Test
    @UiThreadTest
    fun testCompassButton() {
        mapfitMap.getMapOptions().compassButtonEnabled = true
        Assert.assertEquals(View.VISIBLE, mapView.btnCompass.visibility)
    }

    @Test
    @UiThreadTest
    fun testZoomControls() {
        mapfitMap.getMapOptions().zoomControlsEnabled = true
        Assert.assertEquals(View.VISIBLE, mapView.zoomControlsView.visibility)
    }

    @Test
    @UiThreadTest
    fun testRecenterButton() {
        mapfitMap.getMapOptions().recenterButtonEnabled = true
        Assert.assertEquals(View.VISIBLE, mapView.btnRecenter.visibility)
    }

    @Test
    @UiThreadTest
    fun testUserLocationButton() {
        mapfitMap.getMapOptions().userLocationButtonEnabled = true
        Assert.assertEquals(View.VISIBLE, mapView.btnUserLocation.visibility)
    }


}
