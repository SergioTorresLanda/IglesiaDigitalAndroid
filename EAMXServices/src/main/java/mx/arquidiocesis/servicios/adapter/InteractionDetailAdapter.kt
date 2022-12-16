package mx.arquidiocesis.servicios.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.databinding.CardServicesAdminInteractionDetailBinding
import mx.arquidiocesis.servicios.model.IntentionDetail

class InteractionDetailAdapter(
    val list : List<IntentionDetail>
) : RecyclerView.Adapter<InteractionDetailAdapter.AdminServicesAdapterHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminServicesAdapterHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardServicesAdminInteractionDetailBinding.inflate(inflater, parent, false)
        return AdminServicesAdapterHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminServicesAdapterHolder, position: Int) {
        return holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class AdminServicesAdapterHolder(val binding : CardServicesAdminInteractionDetailBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(intention : IntentionDetail){
            binding.apply {
                item = intention
                Log.d("Intencion",intention.toString())
                val adapter = ArrayAdapter(binding.root.context, android.R.layout.simple_list_item_1, intention.names)
                rvMentions.adapter = adapter
            }
        }
    }
}