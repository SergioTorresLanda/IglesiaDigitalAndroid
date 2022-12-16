package mx.arquidiocesis.eamxprofilemodule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxprofilemodule.databinding.ItemServiceDetailBinding
import mx.arquidiocesis.eamxprofilemodule.model.ServiceModel

class ServiceDetailAdapter(val services: ArrayList<ServiceModel>) :
    RecyclerView.Adapter<ServiceDetailAdapter.ServiceDetailAdapterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ServiceDetailAdapterViewHolder {
        val binding = ItemServiceDetailBinding.inflate(LayoutInflater.from(parent.context))
        return ServiceDetailAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceDetailAdapterViewHolder, position: Int) =
        holder.bind(services[position])

    override fun getItemCount() = services.size

    inner class ServiceDetailAdapterViewHolder(val binding: ItemServiceDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(service: ServiceModel) {
            binding.tvService.text = service.name
        }
    }
}