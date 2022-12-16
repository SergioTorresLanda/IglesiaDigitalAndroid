package com.wallia.eamxcomunidades.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.databinding.ItemCommunityBinding
import com.wallia.eamxcomunidades.model.Location
import mx.arquidiocesis.eamxcommonutils.util.convertToBitmap
import mx.arquidiocesis.eamxcommonutils.util.log

class ComunityAdapter(
    var list: List<Location>,
    val context: Context,
    val listener: (Location) -> Unit,
) :
    RecyclerView.Adapter<ComunityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommunityBinding.inflate(inflater, parent, false)
        return ComunityAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(list[position], listener, context)

    override fun getItemCount(): Int = list.size

    class ViewHolder(val binding: ItemCommunityBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Location, listener: (Location) -> Unit, context: Context) {
            binding.apply {

                item.arrayImage?.let {
                    ivCommunity.setImageBitmap(it.convertToBitmap())
                }?: kotlin.run {
                    if (item.imageUrl.isNullOrEmpty()) {
                        Glide.with(context)
                            .load(R.drawable.default_image)
                            .into(ivCommunity)
                    } else {
                        Glide.with(context)
                            .load(Uri.parse(item.imageUrl))
                            .into(ivCommunity)
                    }
                }

                tvTitle.text = item.name
                cvComunity.setOnClickListener {
                    listener(item)
                    "FLOW EAMXRegisterCommunityFragment ComunityAdapter".log()
                }
            }


        }
    }
}