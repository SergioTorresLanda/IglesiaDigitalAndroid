package com.wallia.eamxcomunidades.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.wallia.eamxcomunidades.databinding.ItemEventBinding
import com.wallia.eamxcomunidades.model.ServiceActivityModel
import com.wallia.eamxcomunidades.utils.PublicFunctions

class ServiceActivityAdapter(
    val socialMediaList: MutableList<ServiceActivityModel>,
    var recyclerView: RecyclerView,
    private var isList: Boolean = false,
) :
    RecyclerView.Adapter<ServiceActivityAdapter.SocialMediaViewHolder>() {

    var isFinishServices = MutableLiveData<Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialMediaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventBinding.inflate(inflater, parent, false)
        if (isList != null) {
            if (isList) {
                binding.ivClose.visibility = View.GONE
            }
        }
        return SocialMediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SocialMediaViewHolder, position: Int) {
        val item = socialMediaList[position]
        holder.bind(item) {
            deleteReceiptsList(position)
        }
    }

    private fun deleteReceiptsList(position: Int) {
        //val index = if(position > 0) position - 1 else 0
        socialMediaList.removeAt(position)
        recyclerView.post {
            notifyItemRemoved(position)
            notifyDataSetChanged()
        }

        chekList()
    }

    fun addItem(item: ServiceActivityModel) {
        socialMediaList.add(item)
        recyclerView.post { notifyItemInserted(socialMediaList.size - 1) }

        chekList()
    }

    fun chekList() {
        isFinishServices.value = true
    }

    override fun getItemCount(): Int = socialMediaList.size

    class SocialMediaViewHolder(val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ServiceActivityModel,
            listener: () -> Any,
        ) {
            binding.apply {

                val daysStr = item.scheduleDays
                val dayList = daysStr.split(" - ")

                tvEventDay.text = if (dayList.size > 1 && dayList[0] == dayList[1]) {
                    dayList[0]
                } else {
                    daysStr
                }
                itemService = item
                ivClose.setOnClickListener {
                    listener()
                }
            }
        }
    }
}