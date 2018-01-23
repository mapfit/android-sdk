package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.geometry.LatLng
import com.mapfit.mapfitsdk.utils.isValid
import com.mapfit.mapfitsdk.utils.isValidImageUrl
import com.mapfit.mapfitsdk.utils.isValidZoomLevel
import com.mapfit.mapfitsdk.utils.loadImageFromUrl
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert
import org.junit.Test

/**
 * Unit tests fot [LatLng].
 *
 * Created by dogangulcan on 1/8/18.
 */
class LatLngTest {

    @Test
    fun testValidLatLng() {
        // default
        val defaultLatlng = LatLng()
        Assert.assertEquals(0.0, defaultLatlng.lat + defaultLatlng.lon, 0.0)

        // valid input
        val validLatlng = LatLng(40.693825, -73.998691)
        Assert.assertEquals(40.693825, validLatlng.lat, 0.00001)
        Assert.assertEquals(-73.998691, validLatlng.lon, 0.00001)
        Assert.assertTrue(validLatlng.isValid())

        // invalid input
        val invalidLatlng = LatLng(140.693825, -731.998691)
        Assert.assertFalse(invalidLatlng.isValid())

    }

}