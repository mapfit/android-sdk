package com.mapfit.mapfitdemo.ui.adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mapfit.mapfitdemo.R
import com.mapfit.mapfitdemo.data.model.Filter
import com.mapfit.mapfitdemo.ui.adapter.vh.ButtonFilterVH
import com.mapfit.mapfitdemo.ui.adapter.vh.SpinnerFilterVH
import com.mapfit.mapfitdemo.ui.adapter.vh.SwitchFilterVH

/**
 * Created by dogangulcan on 12/27/17.
 */
class FilterAdapter(private val onFilterChecked: OnFilterCheckedListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val filters = mutableListOf<Filter<*>>()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is SwitchFilterVH -> holder.bind(filters[position])
            is ButtonFilterVH -> holder.bind(filters[position])
            is SpinnerFilterVH -> holder.bind(filters[position] as Filter<List<String>>)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? =

        when (FilterType.values()[viewType]) {
            FilterType.PAN_GESTURE,
            FilterType.ROTATE_GESTURE,
            FilterType.PINCH_GESTURE,
            FilterType.TILT_GESTURE,
            FilterType.ZOOM_CONTROLS,
            FilterType.COFFEE_SHOPS,
            FilterType.RECENTER,
            FilterType.COMPASS,
            FilterType.ALL_MARKERS,
            FilterType.ALWAYS_OPEN -> {
                val itemView = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.list_item_filter_switch, parent, false)
                SwitchFilterVH(itemView, onFilterChecked)
            }
            FilterType.DRAW_ROUTE,
            FilterType.CLEAR_MARKERS -> {
                val itemView = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.list_item_filter_button, parent, false)
                ButtonFilterVH(itemView, onFilterChecked)
            }
            FilterType.MAP_THEME -> {
                val itemView = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.list_item_filter_spinner, parent, false)
                SpinnerFilterVH(itemView, onFilterChecked)
            }

            else -> {
                null
            }
        }

    override fun getItemViewType(position: Int): Int {
        return filters[position].filterType.ordinal
    }

    override fun getItemCount(): Int = filters.size

    fun addItems(filters: List<Filter<*>>) = this.filters.addAll(filters)


}