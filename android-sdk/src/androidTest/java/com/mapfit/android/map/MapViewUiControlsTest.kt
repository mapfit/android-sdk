package com.mapfit.android.map

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.mapfit.android.*
import kotlinx.coroutines.experimental.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for UI controls.
 *
 * Created by dogangulcan on 1/8/18.
 */
@RunWith(AndroidJUnit4::class)
class MapViewUiControlsTest {

    private lateinit var mapView: MapView
    private lateinit var mapfitMap: MapfitMap

    @Rule
    @JvmField
    val activityRule: ActivityTestRule<MapViewTestActivity> = ActivityTestRule(
        MapViewTestActivity::class.java
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
    fun testDefaultValues() = runBlocking {
        Assert.assertEquals(View.GONE, mapView.zoomControlsView.visibility)
        Assert.assertEquals(View.GONE, mapView.btnRecenter.visibility)
        Assert.assertEquals(View.GONE, mapView.btnCompass.visibility)
        Assert.assertEquals(View.GONE, mapView.btnUserLocation.visibility)
        Assert.assertEquals(View.VISIBLE, mapView.attributionImage.visibility)
    }

    @Test
    @UiThreadTest
    fun testAttribution() {
        Assert.assertEquals(View.VISIBLE, mapView.attributionImage.visibility)
    }

    @Test
    @UiThreadTest
    fun testCompassButton() {
        mapfitMap.getMapOptions().isCompassButtonVisible = true
        Assert.assertEquals(View.VISIBLE, mapView.btnCompass.visibility)
    }

    @Test
    @UiThreadTest
    fun testZoomControls() {
        mapfitMap.getMapOptions().isZoomControlVisible = true
        Assert.assertEquals(View.VISIBLE, mapView.zoomControlsView.visibility)
    }

    @Test
    @UiThreadTest
    fun testRecenterButton() {
        mapfitMap.getMapOptions().isRecenterButtonVisible = true
        Assert.assertEquals(View.VISIBLE, mapView.btnRecenter.visibility)
    }

    @Test
    @UiThreadTest
    fun testUserLocationButton() {
        mapfitMap.getMapOptions().isUserLocationButtonVisible = true
        Assert.assertEquals(View.VISIBLE, mapView.btnUserLocation.visibility)
    }

}
