package com.mapfit.android

import android.graphics.drawable.Animatable
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.anim.AnimationListener
import com.mapfit.android.camera.Cinematography
import com.mapfit.android.camera.OrbitTrajectory
import com.mapfit.android.geometry.LatLng
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
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
                , object : AnimationListener {
                    override fun onStart(animatable: Animatable) {
                        idlingResource.decrement()
                    }

                    override fun onFinish(animatable: Animatable) {
                        idlingResource.decrement()
                    }
                }
            )

        idlingResource.increment()
        anim.start()
        suspendViaGLSurface() // wait for onStart

        idlingResource.increment() // wait for onFinish
        suspendViaGLSurface()

        Assert.assertNotSame(0f, mapfitMap.getRotation()) // check if camera rotated
    }

}