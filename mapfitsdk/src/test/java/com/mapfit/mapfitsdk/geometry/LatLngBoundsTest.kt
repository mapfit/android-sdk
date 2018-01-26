package com.mapfit.mapfitsdk.geometry

import junit.framework.Assert
import org.junit.Before
import org.junit.Test

/**
 * Created by dogangulcan on 1/25/18.
 */
class LatLngBoundsTest {

    private lateinit var bounds: LatLngBounds

    @Before
    fun init() {
        val latLngList = listOf(
                LatLng(37.198504, -83.272133),
                LatLng(29.652243, -29.042111),
                LatLng(38.246623, -82.737144),
                LatLng(36.691771, -110.030517),
                LatLng(37.940202, -107.461721),
                LatLng(39.400789, -80.243273))

        val boundsBuilder = LatLngBounds.Builder()

        latLngList.forEach {
            boundsBuilder.include(it)
        }

        bounds = boundsBuilder.build()
    }

    @Test
    fun testSWandNE() {
        val expectedNE = LatLng(39.400789, -29.042111)
        val expectedSW = LatLng(29.652243, -110.030517)

        Assert.assertEquals(expectedNE, bounds.northEast)
        Assert.assertEquals(expectedSW, bounds.southWest)
    }

    @Test
    fun testZoomLevelAndCenter() {
        val expectedCenter = LatLng(42.09842441252814, -72.40426108071209)
        val expectedZoomLevel = 2.7180634f

        val viewWidth = 1440
        val viewHeight = 2194

        val (center, zoomLevel) = bounds.getVisibleBounds(viewWidth, viewHeight)

        Assert.assertEquals(expectedCenter, center)
        Assert.assertEquals(expectedZoomLevel, zoomLevel)
    }

}