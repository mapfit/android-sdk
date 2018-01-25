package com.mapfit.mapfitdemo.ui.adapter.vh

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.mapfit.mapfitdemo.R
import com.mapfit.mapfitdemo.data.model.Filter
import com.mapfit.mapfitdemo.ui.adapter.OnFilterCheckedListener


/**
 * ViewHolder for spinner filters.
 *
 * Created by dogangulcan on 12/27/17.
 */
class SpinnerFilterVH(
        view: View,
        val onFilterChecked: OnFilterCheckedListener
) : RecyclerView.ViewHolder(view) {

    private val spinner = view.findViewById<Spinner>(R.id.spinner)

    fun bind(filter: Filter<List<String>>) {
        val adapter = ArrayAdapter<String>(itemView.context,
                android.R.layout.simple_list_item_1, filter.data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.setSelection(0,false)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onFilterChecked.onSpinnerItemSelected(
                        filter.filterType,
                        filter.data?.get(position) ?: ""
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

}