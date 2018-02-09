package com.mapfit.mapfitsdk.exceptions

/**
 * Exception thrown when authorization is not valid.
 *
 * Created by dogangulcan on 1/18/18.
 */
class MapfitAuthorizationException : RuntimeException(
    "\nMapfit API key is not authorized." +
            "\nPlease visit https://mapfit.com/getstarted to get a new API key.\n"
)