package mx.arquidiocesis.servicios.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.databinding.ItemChurchMentionBinding
import mx.arquidiocesis.servicios.model.IgleciasModel
import java.util.*
import kotlin.collections.ArrayList

class ChurchFindAdapter(
    val churches: List<IgleciasModel>,
    val listenerItem: (IgleciasModel) -> Unit
) : RecyclerView.Adapter<ChurchFindAdapter.ViewHolder>(), Filterable {

    var churchFilterList: List<IgleciasModel>

    init {
        churchFilterList = churches
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
        holder.bind(churchFilterList.get(position), listenerItem)

    override fun getItemCount(): Int = churchFilterList.size

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val charSearch = constraint.toString()
            if (charSearch.isEmpty()) {
                churchFilterList = churches
            } else {
                val resultList = ArrayList<IgleciasModel>()
                for (row in churches) {
                    if ((row.name+row.address).toLowerCase(Locale.ROOT)
                            .contains(charSearch.toLowerCase(Locale.ROOT))
                    )
                        resultList.add(row)
                }
                churchFilterList = resultList
            }
            val filterResults = FilterResults()
            filterResults.values = churchFilterList
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            churchFilterList = results?.values as List<IgleciasModel>
            notifyDataSetChanged()
        }
    }

    class ViewHolder(val binding: ItemChurchMentionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(church: IgleciasModel, listenerItem: (IgleciasModel) -> Unit) {
            binding.apply {
                tvChurchName.text = church.name
                tvAddressName.text = church.address
                if (church?.image_url != null) {
                    Glide.with(binding.root.context)
                        .load(Uri.parse(church.image_url))
                        .into(binding.imageView3)
                } else {
                    binding.imageView3.setImageDrawable(binding.root.context.getDrawable(R.drawable.emptychurch))
                }


                root.setOnClickListener {
                    listenerItem(church)
                }
            }
        }
    }
}