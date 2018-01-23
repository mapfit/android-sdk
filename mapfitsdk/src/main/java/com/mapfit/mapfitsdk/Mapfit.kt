package com.mapfit.mapfitsdk

import android.content.Context
import com.mapfit.mapfitsdk.exceptions.MapfitConfigurationException
import android.text.TextUtils


/**
 * Created by dogangulcan on 12/18/17.
 */
class Mapfit private constructor(private val context: Context?, apiKey: String) {


    companion object {

        @Volatile
        private var mapfitInstance: Mapfit? = null

        @Volatile
        private var appContext: Context? = null

        private lateinit var MAPFIT_API_KEY: String

        @JvmStatic
        fun getInstance(context: Context? = null, apiKey: String): Mapfit {
            MAPFIT_API_KEY = apiKey
            appContext = context
            return synchronized(this) {
                if (mapfitInstance == null) {
                    mapfitInstance = Mapfit(context, apiKey)
                }
                mapfitInstance as Mapfit
            }
        }

        @JvmStatic
        fun getApiKey(): String {
            validateMapfit()
            validateApiKey()
            return MAPFIT_API_KEY
        }

        fun getContext(): Context? = appContext

        private fun validateMapfit() {
            if (mapfitInstance == null) {
                throw MapfitConfigurationException()
            }
        }

        private fun validateApiKey() {
            val apiKey = MAPFIT_API_KEY
            if (TextUtils.isEmpty(apiKey)) {
                throw MapfitConfigurationException()
            }
        }


    }

}