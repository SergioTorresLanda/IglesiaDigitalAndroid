package mx.arquidiocesis.eamxevent.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import mx.arquidiocesis.eamxevent.model.catalog.Zone

class AdapterSpinnerZone(context: Context, resource: Int, private val objects: List<Zone>)
    : ArrayAdapter<Zone>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label =  super.getView(position, convertView, parent) as TextView
        label.apply {
            text = getItem(position).zone.toString()
            setTextColor(parent.context.getColor(android.R.color.black))
        }
        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label =  super.getView(position, convertView, parent) as TextView
        label.apply {
            text = getItem(position).zone.toString()
            setTextColor(parent.context.getColor(android.R.color.black))
        }
        return label
    }

    override fun getCount() = objects.size

    override fun getItem(position: Int) = objects[position]
}

