package com.mapfit.mapfitdemo.ui.adapter.vh

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.View
import com.mapfit.mapfitdemo.R
import com.mapfit.mapfitdemo.data.model.Filter
import com.mapfit.mapfitdemo.ui.adapter.OnFilterCheckedListener

/**
 * ViewHol
 *
 * Created by dogangulcan on 12/27/17.
 */
class FilterVH(view: View, val onFilterChecked: OnFilterCheckedListener) : RecyclerView.ViewHolder(view) {

    val switch = view.findViewById<SwitchCompat>(R.id.filterSwitch)

    fun bind(filter: Filter) {

        switch.text = filter.title
        switch.isChecked = filter.isActive
        switch.setOnCheckedChangeListener { _, isChecked ->
            onFilterChecked.onFilterChecked(filter.filterType, isChecked)
        }

    }

}