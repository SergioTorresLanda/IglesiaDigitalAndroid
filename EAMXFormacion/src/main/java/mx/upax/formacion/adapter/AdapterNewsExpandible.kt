package mx.upax.formacion.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.upax.formacion.R
import com.upax.formacion.databinding.ItemBodyTitleExpandibleBinding
import com.upax.formacion.databinding.ItemTitleExpandibleBinding
import mx.upax.formacion.model.BaseModel

class CustomExpandableListAdapter internal constructor(
    private val context: Context,
    private val titleList: List<String>,
    private val dataList: HashMap<String, List<BaseModel>>,
    private val listener : (BaseModel) -> Unit
) : BaseExpandableListAdapter() {

    var clickIItem : Boolean = false

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return this.dataList[this.titleList[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ) = ItemBodyTitleExpandibleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        .apply {
            dataList[titleList[listPosition]]?.let {
                rvItems.adapter = OthersNewAdapter(it, listener)
            }
        }.root

    override fun getChildrenCount(listPosition: Int): Int {
        return 1 // Se deja en uno para poder extraer la lista de un solo hijo y no repetir los datos
    }

    override fun getGroup(listPosition: Int): Any {
        return this.titleList[listPosition]
    }


    override fun getGroupCount(): Int {
        return this.titleList.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val title = getGroup(listPosition) as String
        val inflater = LayoutInflater.from(parent?.context)
        val binding = ItemTitleExpandibleBinding.inflate(inflater, parent, false)
        binding.tvTitleExpandible.text = title

        if (isExpanded) {
            binding.ivDonwFlecha.setImageResource(R.drawable.ic_icono_atr_d)
        } else {
            binding.ivDonwFlecha.setImageResource(R.drawable.ic_icono_atr_u)
        }

        return binding.root
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}