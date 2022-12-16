package mx.arquidiocesis.eamxbiblioteca.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxbiblioteca.databinding.ItemHighlightBinding
import mx.arquidiocesis.eamxbiblioteca.models.Featured

class HighlightAdapter(val featured: List<Featured>, val listener: (Featured) -> Unit) :
    RecyclerView.Adapter<HighlightAdapter.HighlightViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HighlightViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHighlightBinding.inflate(inflater, parent, false)
        return HighlightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HighlightViewHolder, position: Int) =
        holder.bind(featured[position], listener)

    override fun getItemCount(): Int = featured.size

    class HighlightViewHolder(val binding: ItemHighlightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(featured: Featured, listener: (Featured) -> Unit) {
            binding.apply {

                Glide.with(binding.root.context)
                    .load(Uri.parse(featured.image))
                    .into(ivHighlight)

                tvCountHighlight.text = "${featured.views} Consultas"
                tvNameHighlight.text = featured.title

                root.setOnClickListener {
                    listener(featured)
                }
            }
        }
    }
}