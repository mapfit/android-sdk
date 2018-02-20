package com.mapfit.mapfitsdk

import org.jetbrains.annotations.NotNull

/**
 * Callback for obtaining [MapfitMap] instance to manipulate the map.
 *
 * Created by dogangulcan on 12/18/17.
 */
interface OnMapReadyCallback {

    fun onMapReady(@NotNull mapfitMap: MapfitMap)

}