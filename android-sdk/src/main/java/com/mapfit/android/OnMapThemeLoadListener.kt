package com.mapfit.android

/**
 * Callback to listen map scene load events after changing map theme or updating the scene values.
 *
 * Created by dogangulcan on 3/7/18.
 */
interface OnMapThemeLoadListener {

    fun onLoaded()

    fun onError()

}