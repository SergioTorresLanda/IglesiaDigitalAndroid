package mx.arquidiocesis.servicios.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.servicios.databinding.ItemServicesAdminIntentionBinding
import mx.arquidiocesis.servicios.model.admin.view.AdminIntentionGeneralModel

class InteractionAdapter(
    val list : List<AdminIntentionGeneralModel>,
    val listener: (AdminIntentionGeneralModel) -> Unit
) : RecyclerView.Adapter<InteractionAdapter.AdminServicesAdapterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminServicesAdapterHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServicesAdminIntentionBinding.inflate(inflater, parent, false)
        return AdminServicesAdapterHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminServicesAdapterHolder, position: Int) {
        return holder.bind(list[position], listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class AdminServicesAdapterHolder(val binding : ItemServicesAdminIntentionBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(intention : AdminIntentionGeneralModel, listener: (AdminIntentionGeneralModel) -> Unit){
            binding.apply {
                item = intention
                root.setOnClickListener {
                    listener(intention)
                }
            }
        }
    }
}