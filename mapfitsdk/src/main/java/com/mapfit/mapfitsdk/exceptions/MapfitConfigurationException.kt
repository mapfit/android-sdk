package com.mapfit.mapfitsdk.exceptions

/**
 * Created by dogangulcan on 1/18/18.
 */
class MapfitConfigurationException : RuntimeException(
        "\\nUsing MapView requires setting a valid access token. Use Mapfit.getInstance(context, apiKey)" +
                "to provide one. \"\n" +
                "\\nIf you don't have an api key, please visit https://mapfit.com/getakey to get your api key.\"\n"
)