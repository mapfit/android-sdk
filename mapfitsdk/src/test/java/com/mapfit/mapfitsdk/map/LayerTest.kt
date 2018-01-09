package com.mapfit.mapfitsdk.map

import com.mapfit.mapfitsdk.Layer
import junit.framework.Assert
import org.junit.Test

/**
 * Created by dogangulcan on 1/5/18.
 */
class LayerTest {

    @Test
    fun testLayerDefaultVisibility() {
        val layer = Layer()
        Assert.assertTrue(layer.isVisible)
    }

}