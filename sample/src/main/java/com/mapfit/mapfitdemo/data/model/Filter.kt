package com.mapfit.mapfitdemo.data.model

import com.mapfit.mapfitdemo.ui.adapter.FilterType

/**
 * Created by dogangulcan on 12/27/17.
 */
data class Filter(var filterType: FilterType, var title: String, var isActive: Boolean)