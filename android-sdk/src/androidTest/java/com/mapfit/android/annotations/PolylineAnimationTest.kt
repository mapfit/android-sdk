package com.mapfit.android.annotations

import android.graphics.drawable.Animatable
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.*
import com.mapfit.android.anim.AnimationListener
import com.mapfit.android.anim.polyline.SnakeAnimation
import com.mapfit.android.anim.polyline.SnakeAnimationOptions
import com.mapfit.android.annotations.callback.OnPolylineClickListener
import com.mapfit.android.geometry.LatLng
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for [Polyline] functionality.
 *
 * Created by dogangulcan on 1/17/18.
 */
@RunWith(AndroidJUnit4::class)
class PolylineAnimationTest {

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
    fun testSnakeAnimation() {
        val animationListener = object : AnimationListener {
            override fun onStart(animatable: Animatable) {
                idlingResource.decrement()

            }

            override fun onFinish(animatable: Animatable) {
                idlingResource.decrement()
            }
        }

        val polyline = mapfitMap.addPolyline(
            PolylineOptions()
                .points(line)
                .animation(
                    SnakeAnimation(
                        SnakeAnimationOptions()
                            .duration(500)
                            .animationListener(animationListener)
                    )
                )
        )

        mapfitMap.setLatLngBounds(polyline.getLatLngBounds(), 0.8f)
        mapfitMap.setZoom(16f)

        Assert.assertNotNull(polyline.animation)
        idlingResource.increment()
        idlingResource.increment()

        polyline.animation!!.start()

        suspendViaGLSurface()

        Assert.assertTrue(polyline.animation!!.finished)
        Assert.assertFalse(polyline.animation!!.running)
        Assert.assertFalse(polyline.animation!!.canceled)
    }
}

