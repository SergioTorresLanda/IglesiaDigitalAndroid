package mx.arquidiocesis.servicios.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.servicios.databinding.ItemServicesAdminServicesBinding
import mx.arquidiocesis.servicios.model.admin.view.AdminServiceModel

class AdminServicesAdapter(
    val list : MutableList<AdminServiceModel>,
    val listener: (AdminServiceModel) -> Unit
) : RecyclerView.Adapter<AdminServicesAdapter.AdminServicesAdapterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminServicesAdapterHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServicesAdminServicesBinding.inflate(inflater, parent, false)
        return AdminServicesAdapterHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminServicesAdapterHolder, position: Int) {
        return holder.bind(list[position], listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun removeAt(position : Int){
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clear(){
        list.clear()
        notifyDataSetChanged()
    }

    class AdminServicesAdapterHolder(val binding : ItemServicesAdminServicesBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(servicesItem : AdminServiceModel, listener: (AdminServiceModel) -> Unit){
            binding.apply {
                item = servicesItem
                root.setOnClickListener {
                    listener(servicesItem)
                }
            }
        }
    }
}