package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.utils.isValidImageUrl
import com.mapfit.mapfitsdk.utils.isValidZoomLevel
import com.mapfit.mapfitsdk.utils.loadImageFromUrl
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert
import org.junit.Test

/**
 * Created by dogangulcan on 1/8/18.
 */
class UtilsTest {

    @Test
    fun testValidZoomLevel() {
        Assert.assertTrue(isValidZoomLevel(5f))
        Assert.assertFalse(isValidZoomLevel(-5f))
        Assert.assertFalse(isValidZoomLevel(26f))
    }

    @Test
    fun testLoadImageFromUrl() {
        runBlocking {
            Assert.assertNull(loadImageFromUrl("https://www.google.com").await())
        }
    }

    @Test
    fun testValidImageUrl() {
        Assert.assertFalse(isValidImageUrl("https://www.google.com"))
        Assert.assertFalse(isValidImageUrl(""))
        Assert.assertTrue(isValidImageUrl("https://mapfit.com/images/pngs/featuresGraphic.png"))
    }

}