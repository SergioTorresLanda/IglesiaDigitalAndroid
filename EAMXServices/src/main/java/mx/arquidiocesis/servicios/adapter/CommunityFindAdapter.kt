package mx.arquidiocesis.servicios.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.servicios.databinding.ItemChurchMentionBinding
import mx.arquidiocesis.servicios.model.CommunityResponse
import mx.arquidiocesis.servicios.model.IgleciasModel
import java.util.*
import kotlin.collections.ArrayList

class CommunityFindAdapter(
    val communities: List<CommunityResponse>,
    private val listenerItem: (CommunityResponse) -> Unit
) : RecyclerView.Adapter<CommunityFindAdapter.ViewHolder>(), Filterable {

    var communityFilterList: List<CommunityResponse>

    init {
        communityFilterList = communities
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChurchMentionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(communityFilterList[position], listenerItem)

    override fun getItemCount(): Int = communityFilterList.size

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val charSearch = constraint.toString()
            if (charSearch.isEmpty()) {
                communityFilterList = communities
            } else {
                val resultList = ArrayList<CommunityResponse>()
                for (row in communities) {
                    if (row.name?.toLowerCase(Locale.ROOT)
                            ?.contains(charSearch.toLowerCase(Locale.ROOT)) == true
                    )
                        resultList.add(row)
                }
                communityFilterList = resultList
            }
            val filterResults = FilterResults()
            filterResults.values = communityFilterList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            communityFilterList = results?.values as List<CommunityResponse>
            notifyDataSetChanged()
        }
    }

    class ViewHolder(val binding: ItemChurchMentionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(community: CommunityResponse, listenerItem: (CommunityResponse) -> Unit) {
            binding.apply {
                tvChurchName.text = community.name
                root.setOnClickListener {
                    listenerItem(community)
                }
            }
        }
    }
}