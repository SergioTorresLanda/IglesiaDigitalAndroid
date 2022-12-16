package mx.arquidiocesis.eamxbiblioteca.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxbiblioteca.databinding.ItemFoungBinding
import mx.arquidiocesis.eamxbiblioteca.models.SearchResponse

class FoundAdapter(var resultsFound: List<SearchResponse>, val listener: (SearchResponse) -> Unit) : RecyclerView.Adapter<FoundAdapter.FoundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFoungBinding.inflate(inflater, parent, false)
        return FoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoundViewHolder, position: Int) {
        holder.bind(resultsFound[position], listener)
    }

    override fun getItemCount(): Int = resultsFound.size

    class FoundViewHolder(val binding: ItemFoungBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(searchResponse: SearchResponse, listener: (SearchResponse) -> Unit) {
            binding.tvFounded.text = searchResponse.name
            binding.root.setOnClickListener {
                listener(searchResponse)
            }
        }
    }
}