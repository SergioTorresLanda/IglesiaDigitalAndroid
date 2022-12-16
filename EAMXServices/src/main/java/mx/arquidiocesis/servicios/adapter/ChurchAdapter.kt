package mx.arquidiocesis.servicios.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.databinding.ItemChurchServicesBinding
import mx.arquidiocesis.servicios.model.Location

class ChurchAdapter(
    val context: Context,
    val churches: List<Location>?,
    val listener: (Location) -> Unit
) : RecyclerView.Adapter<ChurchAdapter.ChurchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChurchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChurchServicesBinding.inflate(inflater, parent, false)
        return ChurchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChurchViewHolder, position: Int) =
        holder.bind(context, listener, churches?.get(position))

    override fun getItemCount(): Int = churches!!.size

    class ChurchViewHolder(val binding: ItemChurchServicesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            listener: (Location) -> Unit,
            church: Location?
        ) {
            binding.tvChurch.text = church?.name

            if (church?.image_url != null) {
                Glide.with(binding.root.context)
                    .load(Uri.parse(church.image_url))
                    .into(binding.ivChurch)
            } else {
                binding.ivChurch.setImageDrawable(binding.root.context.getDrawable(R.drawable.emptychurch))
            }

            binding.root.setOnClickListener {
                if (church != null)
                    listener(church)
            }
        }
    }
}