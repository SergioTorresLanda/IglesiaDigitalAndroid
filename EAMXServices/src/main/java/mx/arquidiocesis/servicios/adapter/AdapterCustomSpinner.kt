package mx.arquidiocesis.servicios.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import mx.arquidiocesis.servicios.model.StatusServices

class AdapterCustomSpinner(context: Context, resource: Int, private val objects: List<StatusServices>)
    : ArrayAdapter<StatusServices>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label =  super.getView(position, convertView, parent) as TextView
        label.apply {
            text = getItem(position).name
            //setTextColor(parent.context.getColor(android.R.color.black))
        }
        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label =  super.getView(position, convertView, parent) as TextView
        label.apply {
            text = getItem(position).name
            //setTextColor(parent.context.getColor(android.R.color.black))
        }
        return label
    }

    override fun getCount() = objects.size

    override fun getItem(position: Int) = objects[position]
}
