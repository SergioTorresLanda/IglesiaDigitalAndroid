package com.wallia.eamxcomunidades.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.wallia.eamxcomunidades.R
import com.wallia.eamxcomunidades.databinding.ItemSocialNetworkBinding
import com.wallia.eamxcomunidades.model.SocialMediaModel
import mx.arquidiocesis.eamxcommonutils.util.loadIntDrawable
import okhttp3.internal.notify
import okhttp3.internal.notifyAll

class SocialMediaAdapter(
    val socialMediaList: MutableList<SocialMediaModel>,
    var recyclerView: RecyclerView,
) : RecyclerView.Adapter<SocialMediaAdapter.SocialMediaViewHolder>() {

    var isFinish = MutableLiveData<Boolean>()
    var adapterPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialMediaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSocialNetworkBinding.inflate(inflater, parent, false)
        return SocialMediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SocialMediaViewHolder, position: Int) {

        adapterPosition = position

        val item = socialMediaList[position]
        holder.bind(item) {
            deleteReceiptsList(position)
        }
    }

    private fun deleteReceiptsList(position: Int) {
        val index = position//if (position > 0) position - 1 else 0

        //if (socialMediaList.size == 1)
          //  index = 0

        socialMediaList.removeAt(index)

        recyclerView.post {
            notifyItemRemoved(index)
        }

        notifyDataSetChanged()
        chekList()

    }

    fun addItem(item: SocialMediaModel) {
        when (val position = socialMediaList.indexOfFirst {
            it.nameNetwork == item.nameNetwork
        }) {
            -1 -> {
                socialMediaList.add(item)
                recyclerView.post { notifyItemInserted(socialMediaList.size - 1) }
            }
            else -> {
                socialMediaList[position].value = item.value
                recyclerView.post { notifyItemChanged(position) }
            }
        }
        chekList()
    }

    fun chekList() {
        isFinish.value = true
    }

    override fun getItemCount(): Int = socialMediaList.size

    class SocialMediaViewHolder(val binding: ItemSocialNetworkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: SocialMediaModel,
            listener: () -> Any,
        ) {
            binding.apply {
                valueSocialMedia = item.value
                when (item.nameNetwork) {
                    binding.root.context.getString(R.string.txt_website) -> binding.ivSocialNetwork.loadIntDrawable(
                        R.drawable.icono_www)
                    binding.root.context.getString(R.string.txt_instagram) -> binding.ivSocialNetwork.loadIntDrawable(
                        R.drawable.ic_instragram)
                    binding.root.context.getString(R.string.txt_facebook) -> binding.ivSocialNetwork.loadIntDrawable(
                        R.drawable.icono_facebook)
                    binding.root.context.getString(R.string.txt_twtter) -> binding.ivSocialNetwork.loadIntDrawable(
                        R.drawable.icono_twitter)
                    binding.root.context.getString(R.string.txt_youtube) -> binding.ivSocialNetwork.loadIntDrawable(
                        R.drawable.icon_youtube)

                }
                ivClose.setOnClickListener {
                    listener()
                }
            }
        }
    }
}