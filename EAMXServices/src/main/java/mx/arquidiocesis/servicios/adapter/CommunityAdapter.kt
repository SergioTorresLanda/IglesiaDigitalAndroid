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
import mx.arquidiocesis.servicios.model.LocationXXX

class CommunityAdapter(
    val context: Context,
    val communities: List<LocationXXX>?,
    val listener: (LocationXXX) -> Unit
) : RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChurchServicesBinding.inflate(inflater, parent, false)
        return CommunityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) =
        holder.bind(context, listener, communities?.get(position))

    override fun getItemCount(): Int = communities!!.size

    class CommunityViewHolder(val binding: ItemChurchServicesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            listener: (LocationXXX) -> Unit,
            community: LocationXXX?
        ) {
            //binding.tvAssociation.text = community?.name

            if (community?.imageUrl != null) {
                Glide.with(binding.root.context)
                    .load(Uri.parse(community?.imageUrl))
                    .into(binding.ivChurch)
            } else {
                binding.ivChurch.setImageDrawable(binding.root.context.getDrawable(R.drawable.emptychurch))
            }

            binding.root.setOnClickListener {
                if (community != null)
                    listener(community)
            }
        }
    }
}