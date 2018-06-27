package com.mapfit.android

import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.camera.CameraAnimationCallback
import com.mapfit.android.camera.Cinematography
import com.mapfit.android.camera.OrbitTrajectory
import com.mapfit.android.geometry.LatLng
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class CameraTest {

    private lateinit var mapView: MapView

    private lateinit var mapfitMap: MapfitMap

    @Mock
    private lateinit var onMapPanListener: OnMapPanListener

    private val latLng = LatLng(40.7441855, -73.995394)
    private var idlingResource = CountingIdlingResource("anim_idling_resource")

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
            mapfitMap.setOnMapPanListener(onMapPanListener)
        }

        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun dispose() {
        Mapfit.dispose()
        IdlingRegistry.getInstance().unregister(idlingResource)
    }


    @Test
    fun testOrbitAnimation() {
        val anim = Cinematography(mapfitMap)
            .create(
                OrbitTrajectory()
                    .loop(false)
                    .duration(1000)
                    .pivot(latLng)
                , object : CameraAnimationCallback {
                    override fun onStart() {
                        idlingResource.decrement()
                    }

                    override fun onFinish() {
                        idlingResource.decrement()
                    }
                }
            )

        idlingResource.increment()
        anim.start()
        suspendViaGLSurface() // wait for onStart

        idlingResource.increment() // wait for onFinish
        suspendViaGLSurface()

        Mockito.verify(onMapPanListener, Mockito.atLeastOnce()) // verify map is panned'
    }

}