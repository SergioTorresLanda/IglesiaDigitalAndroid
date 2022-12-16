package mx.arquidiocesis.oraciones.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.recyclerview.widget.LinearLayoutManager
import com.arquidiocesis.oraciones.databinding.ItemGrupOracionBinding
import com.arquidiocesis.oraciones.databinding.ItemTipoOracionBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import mx.arquidiocesis.eamxcommonutils.util.imagen.FunImagen
import mx.arquidiocesis.oraciones.model.OracionesModel
import mx.arquidiocesis.oraciones.model.OracionesTipoModel


class AdapterExpandible(
    var oracionesTipo: MutableList<OracionesTipoModel>,
    var context: Context,
    var oraciones: MutableList<List<OracionesModel>>,
    var expandableListView: ExpandableListView,
    val listenerItem: (OracionesModel) -> Unit
) : BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): Any {
        return oracionesTipo[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return oracionesTipo[groupPosition].id.toLong()
    }

    override fun getGroupCount(): Int {
        return oracionesTipo.size
    }

    fun updateReceiptsItem(items: List<OracionesModel>) {
        oraciones.add(items)
        expandableListView.post(Runnable { notifyDataSetChanged() })
    }

    fun updateReceiptsList(item: MutableList<OracionesTipoModel>) {

        oracionesTipo = item
        oraciones.clear()
        item.forEach {
            updateReceiptsItem(it.devotions)
        }
        expandableListView.post(Runnable { notifyDataSetChanged() })

    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val inflater = LayoutInflater.from(parent?.context)
        val binding = ItemTipoOracionBinding.inflate(inflater, parent, false)
        val oracion = oracionesTipo[groupPosition]
        var file = FunImagen().imagenInterna(context, "oracionTipo"+oracion.id)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val ob =  BitmapDrawable(context.resources, bitmap)

            binding.ivFondoOracion.setBackgroundDrawable(ob)
        } else {
            Glide.with(context).load(Uri.parse(oracion.icon_url))
                .apply(RequestOptions().override(707, 180).fitCenter())
                .into(binding.ivFondoOracion)
            FunImagen().descargaImagen(context,oracion.icon_url,"oracionTipo"+oracion.id)
        }


        if (!isExpanded) {
            expandableListView.expandGroup(groupPosition)
        }
        /*
       */
        return binding.root
    }


    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return oraciones[groupPosition][childPosition]
    }


    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val inflater = LayoutInflater.from(parent?.context)
        val iBinding = ItemGrupOracionBinding.inflate(inflater, parent, false)
        val oracion = oraciones[groupPosition]
        iBinding.rcGrupOracion.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = OracionAdapter(oracion, context) {
                listenerItem(it)
            }
        }


        return iBinding.root
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return oraciones[groupPosition][childPosition].id.toLong()
    }


}