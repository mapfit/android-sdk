package com.mapfit.mapfitdemo.ui.adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mapfit.mapfitdemo.R
import com.mapfit.mapfitdemo.data.model.Filter
import com.mapfit.mapfitdemo.ui.adapter.vh.FilterVH

/**
 * Created by dogangulcan on 12/27/17.
 */
class FilterAdapter(private val onFilterChecked: OnFilterCheckedListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val filters = mutableListOf<Filter>()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is FilterVH -> holder.bind(filters[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_filter, parent, false)
        return FilterVH(itemView, onFilterChecked)
//
//        return when (FilterType.values()[viewType]) {
//            FilterType.ZOOM_CONTROLS,
//            FilterType.ALL_MARKERS,
//            FilterType.ALWAYS_OPEN -> {
//
//            }
//
//        }

    }

    override fun getItemViewType(position: Int): Int {
        return filters[position].filterType.ordinal
    }

    override fun getItemCount(): Int = filters.size

    fun addItems(filters: List<Filter>) = this.filters.addAll(filters)


}