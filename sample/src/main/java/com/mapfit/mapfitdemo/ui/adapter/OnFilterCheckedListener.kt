package com.mapfit.mapfitdemo.ui.adapter

/**
 * Created by dogangulcan on 12/27/17.
 */
interface OnFilterCheckedListener {

    fun onFilterChecked(filterType: FilterType, isChecked: Boolean)

    fun onClearMarkersClicked()

}