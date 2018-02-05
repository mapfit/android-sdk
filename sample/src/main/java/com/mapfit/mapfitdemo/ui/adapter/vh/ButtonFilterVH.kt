package com.mapfit.mapfitdemo.ui.adapter.vh

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.Button
import com.mapfit.mapfitdemo.R
import com.mapfit.mapfitdemo.data.model.Filter
import com.mapfit.mapfitdemo.ui.adapter.FilterType
import com.mapfit.mapfitdemo.ui.adapter.OnFilterCheckedListener

/**
 * ViewHol
 *
 * Created by dogangulcan on 12/27/17.
 */
class ButtonFilterVH(view: View, private val onFilterChecked: OnFilterCheckedListener) :
    RecyclerView.ViewHolder(view) {

    val button = view.findViewById<Button>(R.id.filterButton)

    fun bind(filter: Filter<*>) {

        button.text = filter.title
        button.setOnClickListener {
            when (filter.filterType) {
                FilterType.DRAW_ROUTE -> onFilterChecked.onDrawRouteClicked()
                FilterType.CLEAR_MARKERS -> onFilterChecked.onClearMarkersClicked()
            }
        }

    }

}