package com.example.bloodbank

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class BloodGroupAdapter(context: Context, private val resource: Int, private val objects: Array<String>) : ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        if (position == 0) {
            // Default value, when nothing is selected - make the color to redLight50
            view.setTextColor(context.resources.getColor(R.color.redLight50, null))
        } else {
            // Selected value, when a value is selected - make the color to redLight
            view.setTextColor(context.resources.getColor(R.color.redLight, null))
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView

        // When in the dropdown view, make all the value's color to redLight
        view.setTextColor(context.resources.getColor(R.color.redLight, null))

        return view
    }
}
