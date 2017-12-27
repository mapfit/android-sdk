package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.utils.isValidZoomLevel
import com.mapzen.tangram.MapController

/**
 * Created by dogangulcan on 12/21/17.
 */
class MapOptions(private val tangramMap: MapController) {

    var maxZoom: Float = 20.5f
        set(value) {
            if (isValidZoomLevel(value)) {
                field = value
            }
        }

    var scenePath = MapStyle.MAPFIT_NIGHT // default scene
        set(value) {
            //todo validate
            tangramMap.loadSceneFile(value.toString())
        }


}