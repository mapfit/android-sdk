package com.mapfit.android

/**
 * Interface to listen map scene load events after changing map theme.
 *
 * Created by dogangulcan on 3/7/18.
 */
interface OnMapThemeLoadListener {

    fun onLoaded()

    fun onError()

}