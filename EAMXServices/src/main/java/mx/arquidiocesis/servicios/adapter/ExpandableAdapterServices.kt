package mx.arquidiocesis.servicios.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.databinding.ItemFormationOtroBinding
import mx.arquidiocesis.servicios.databinding.ItemSpinnerBinding
import mx.arquidiocesis.servicios.model.ServicesModel
import mx.arquidiocesis.servicios.model.TitleExpanderListModel

class ExpandableAdapterServices(
    var formationTipo: MutableList<TitleExpanderListModel>,
    var expandableListView: ExpandableListView,
    val listenerItem: (ServicesModel) -> Unit,
) : BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int) = formationTipo[groupPosition]

    override fun getGroupId(groupPosition: Int) = formationTipo[groupPosition].id.toLong()


    override fun getGroupCount() = formationTipo.size

    @SuppressLint("ResourceAsColor")
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ) = ItemFormationOtroBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        .apply {
            ivArrow.setImageResource(if (isExpanded) R.drawable.ic_icono_atr_s else R.drawable.ic_down_arrow)
            tvOracion.text = formationTipo[groupPosition].name

//        if (!isExpanded) {
//            expandableListView.expandGroup(groupPosition)
//        }
        }.root

    fun updateReceiptsItem(id: Int, items: MutableList<ServicesModel>) {
        formationTipo[id].service = items
        expandableListView.post { notifyDataSetChanged() }
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = false

    override fun hasStableIds() = false

    override fun getChildrenCount(groupPosition: Int) = formationTipo[groupPosition].service.size


    override fun getChild(groupPosition: Int, childPosition: Int) =
        formationTipo[groupPosition].service[childPosition]

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ) = ItemSpinnerBinding.inflate(LayoutInflater.from(parent?.context), parent, false).apply {
        val oracion = formationTipo[groupPosition].service[childPosition]
        tvOracion.text = oracion.name
        clOracioItem.setOnClickListener { listenerItem(oracion) }
    }.root


    override fun getChildId(groupPosition: Int, childPosition: Int) =
        formationTipo[groupPosition].service[childPosition].id.toLong()

}