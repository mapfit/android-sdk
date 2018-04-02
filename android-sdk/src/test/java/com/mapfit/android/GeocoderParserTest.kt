package com.mapfit.android

import com.mapfit.android.geocoder.GeocodeParser
import com.mapfit.android.geometry.isEmpty
import org.junit.Assert
import org.junit.Test

/**
 * Created by dogangulcan on 1/22/18.
 */
class GeocoderParserTest {

    @Test
    fun parseResponse() {
        val parser = GeocodeParser()
        val addressList = parser.parseGeocodeResponse(response)

        Assert.assertTrue(addressList.isNotEmpty())

        val firstAddress = addressList[0]

        Assert.assertTrue(firstAddress.streetAddress.isNotBlank())
        Assert.assertTrue(firstAddress.adminArea.isNotBlank())
        Assert.assertNotNull(firstAddress.responseType)
        Assert.assertTrue(firstAddress.locality.isNotBlank())
        Assert.assertTrue(firstAddress.neighborhood.isNotBlank())
        Assert.assertTrue(firstAddress.postalCode.isNotBlank())
        Assert.assertEquals(0.0, firstAddress.lat, 0.0001)
        Assert.assertEquals(0.0, firstAddress.lng, 0.0001)
        Assert.assertTrue(firstAddress.postalCode.isNotBlank())
        Assert.assertTrue(firstAddress.building.polygon.isNotEmpty())
        Assert.assertFalse(firstAddress.viewport!!.southWest.isEmpty())
        Assert.assertFalse(firstAddress.viewport!!.northEast.isEmpty())

        val entrance = firstAddress.entrances[0]

        Assert.assertNotNull(entrance.entranceType)
        Assert.assertEquals(40.74405, entrance.lat, 0.0001)
        Assert.assertEquals(-73.99324, entrance.lng, 0.0001)

    }

    private val response =
        "[{ \"locality\": \"New York\", \"postal_code\": \"10011\", \"admin_1\": \"NY\", \"viewport\": { \"southwest\": { \"lon\": -73.97458, \"lat\": 40.6351 }, \"northeast\": { \"lon\": -73.92457999, \"lat\": 40.6651 } },\"country\" : \"USA\", \"neighborhood\": \"chelsea\", \"response_type\": 1, \"building\": { \"type\": \"Polygon\", \"coordinates\": [[[-73.992953, 40.744257], [-73.993265, 40.744389], [-73.993448, 40.744138], [-73.993136, 40.744006], [-73.992953, 40.744257]]] }, \"street_address\": \"119 W 24th St\", \"entrances\": [{ \"lon\": -73.99324, \"lat\": 40.74405, \"entrance_type\": \"pedestrian-primary\" }] }]"


}