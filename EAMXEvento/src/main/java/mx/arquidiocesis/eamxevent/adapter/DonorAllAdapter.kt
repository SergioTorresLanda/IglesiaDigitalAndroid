package mx.arquidiocesis.eamxevent.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxevent.databinding.ItemEventDetailBinding
import mx.arquidiocesis.eamxevent.model.DinerResponse
import mx.arquidiocesis.eamxevent.model.VolunteerResponse

class DonorAllAdapter(
    val context: Context,
    val participation: Int = 0
) : RecyclerView.Adapter<DonorAllAdapter.NewsViewHolder>(), Filterable {

    var items: ArrayList<VolunteerResponse> = ArrayList()
    lateinit var onItemClickListener: (VolunteerResponse, String) -> Unit


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DonorAllAdapter.NewsViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: DonorAllAdapter.NewsViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class NewsViewHolder(private val binding: ItemEventDetailBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(item: DinerResponse) = with(binding) {
        }
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }
}