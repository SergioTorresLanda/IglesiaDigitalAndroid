package com.wallia.eamxcomunidades.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.wallia.eamxcomunidades.databinding.ItemCommunitySearchBinding
import com.wallia.eamxcomunidades.model.CommunitiesByNameResponseItem
import com.wallia.eamxcomunidades.model.Location
import mx.arquidiocesis.eamxcommonutils.util.log

class ComunitySearchAdapter(
    var list: List<CommunitiesByNameResponseItem>,
    val context: Context,
    val listener: (CommunitiesByNameResponseItem) -> Unit,
) :
    RecyclerView.Adapter<ComunitySearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommunitySearchBinding.inflate(inflater, parent, false)
        return ComunitySearchAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(list[position], listener, context)

    override fun getItemCount(): Int = list.size

    class ViewHolder(val binding: ItemCommunitySearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CommunitiesByNameResponseItem,
            listener: (CommunitiesByNameResponseItem) -> Unit,
            context: Context
        ) {
            binding.apply {

                tvTitle.text = item.name
                tvContent.text = item.instituteOrAssociation
                tvData.text = "${item.address} \n" +
                        "${item.phone}"

                cvComunity.setOnClickListener {
                    listener(item)
                    "FLOW EAMXRegisterCommunityFragment ComunitySearchAdapter".log()
                }
            }


        }
    }
}