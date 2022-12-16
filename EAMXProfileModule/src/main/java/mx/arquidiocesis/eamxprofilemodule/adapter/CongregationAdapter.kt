package mx.arquidiocesis.eamxprofilemodule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxprofilemodule.databinding.ItemCongregationProfileBinding
import mx.arquidiocesis.eamxprofilemodule.model.CongregationModel
import java.util.*
import kotlin.collections.ArrayList

class CongregationAdapter(
    val congregations: List<CongregationModel>,
    val listener: (CongregationModel) -> Unit
) : RecyclerView.Adapter<CongregationAdapter.ViewHolder>(), Filterable {

    var countryFilterList: List<CongregationModel>

    init {
        countryFilterList = congregations
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCongregationProfileBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(countryFilterList.get(position), listener)
    }

    override fun getItemCount(): Int = countryFilterList.size

    class ViewHolder(val binding: ItemCongregationProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(congregation: CongregationModel, listenerItem: (CongregationModel) -> Unit) {
            binding.tvCongregation.text = congregation.description

            binding.root.setOnClickListener {
                listenerItem(congregation)
            }
        }
    }

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val charSearch = constraint.toString()
            if (charSearch.isEmpty()) {
                countryFilterList = congregations
            } else {
                val resultList = ArrayList<CongregationModel>()
                for (row in congregations) {
                    if ((row.description?.toLowerCase(Locale.ROOT)
                            ?: "").contains(charSearch.toLowerCase(Locale.ROOT))
                    )
                        resultList.add(row)
                }
                countryFilterList = resultList
            }
            val filterResults = FilterResults()
            filterResults.values = countryFilterList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            countryFilterList = results?.values as List<CongregationModel>
            notifyDataSetChanged()
        }
    }
}