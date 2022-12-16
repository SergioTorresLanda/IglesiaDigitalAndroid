package com.wallia.eamxcomunidades.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wallia.eamxcomunidades.databinding.ItemCommunityRegisterBinding
import com.wallia.eamxcomunidades.model.CommunityLinkedModel

class CommunityLinkedAdapter(
    private val socialMediaList: MutableList<CommunityLinkedModel>,
    var recyclerView: RecyclerView
) :
    RecyclerView.Adapter<CommunityLinkedAdapter.SocialMediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialMediaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommunityRegisterBinding.inflate(inflater, parent, false)
        return SocialMediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SocialMediaViewHolder, position: Int) {
        val item = socialMediaList[position]
        holder.bind(item){
            deleteReceiptsList(position)
        }
    }

    private fun deleteReceiptsList(position: Int) {
        socialMediaList.removeAt(position)
        recyclerView.post { notifyItemRemoved(position) }
    }

    fun addItem(item: CommunityLinkedModel) {
        socialMediaList.add(item)
        recyclerView.post { notifyItemInserted(socialMediaList.size - 1) }
    }

    override fun getItemCount(): Int = socialMediaList.size

    class SocialMediaViewHolder(val binding: ItemCommunityRegisterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CommunityLinkedModel,
            listener : () -> Any
        ) {
            binding.apply {
                itemCommunity = item
                ivClose.setOnClickListener {
                    listener()
                }
            }
        }
    }
}