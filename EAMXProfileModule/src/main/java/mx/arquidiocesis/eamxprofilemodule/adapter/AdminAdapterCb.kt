package mx.arquidiocesis.eamxprofilemodule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxprofilemodule.databinding.ItemAdminRbBinding
import mx.arquidiocesis.eamxprofilemodule.model.CollaboratorModel
import java.util.*
import kotlin.collections.ArrayList

class AdminAdapterCb(
    val admins: ArrayList<CollaboratorModel>,
    val listener: (CollaboratorModel) -> Unit,
) : RecyclerView.Adapter<AdminAdapterCb.AdminAdapterCbViewHolder>(), Filterable {
    private var adminsFilterList: List<CollaboratorModel>

    init {
        adminsFilterList = admins
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AdminAdapterCb.AdminAdapterCbViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAdminRbBinding.inflate(inflater)
        return AdminAdapterCbViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminAdapterCb.AdminAdapterCbViewHolder, position: Int) =
        holder.bind(adminsFilterList[position], listener)

    override fun getItemCount() = adminsFilterList.size

    inner class AdminAdapterCbViewHolder(val binding: ItemAdminRbBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(admin: CollaboratorModel, listener: (CollaboratorModel) -> Unit) {

            binding.cbAdmin.text = "${admin.name} ${admin.firstSurname}"

            binding.root.setOnClickListener {
                listener(admin)
            }
        }
    }

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val charSearch = constraint.toString()
            if (charSearch.isEmpty()) {
                adminsFilterList = admins
            } else {
                val resultList = ArrayList<CollaboratorModel>()
                for (row in admins) {
                    if (row.name!!.toLowerCase(Locale.ROOT)
                            .contains(charSearch.toLowerCase(Locale.ROOT))
                    )
                        resultList.add(row)
                }
                adminsFilterList = resultList
            }
            val filterResults = FilterResults()
            filterResults.values = adminsFilterList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            adminsFilterList = results?.values as List<CollaboratorModel>
            notifyDataSetChanged()
        }
    }
}