package com.mapfit.android

import android.graphics.Color
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.geometry.LatLng
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class BuildingExtrutionTest {

    private lateinit var mapfitMap: MapfitMap
    private lateinit var mapView: MapView

    private val latLng = LatLng(40.740939, -73.989676)

    private var idlingResource = CountingIdlingResource("building_idling_resource")

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
        mapfitMap.apply {
            setCenter(latLng)
            setZoom(19f)
        }

        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    @UiThreadTest
    fun dispose() {
        Mapfit.dispose()
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testBuildingExtrution() = runBlocking {
        delay(500)
        mapfitMap.extrudeBuilding(latLng)
        delay(2000)

        var triple = Triple(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

        idlingResource.increment()
        mapView.getMapSnap(MapController.FrameCaptureCallback {
            val screenPosition = mapView.getScreenPosition(latLng)
            val pixel = it.getPixel(screenPosition.x.toInt(), screenPosition.y.toInt())
            triple = Triple(Color.red(pixel), Color.green(pixel), Color.blue(pixel))
            idlingResource.decrement()

        })

        suspendViaGLSurface()
        Assert.assertEquals(235, triple.first) // light filters the color hence its not 255
        Assert.assertEquals(0, triple.second)
        Assert.assertEquals(0, triple.third)
    }
}
