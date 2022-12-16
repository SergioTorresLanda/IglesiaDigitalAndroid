package mx.arquidiocesis.eamxprofilemodule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxprofilemodule.databinding.ItemModuleDetailBinding
import mx.arquidiocesis.eamxprofilemodule.model.Module

class ModuleDetailAdapter(val modules: ArrayList<Module>) :
    RecyclerView.Adapter<ModuleDetailAdapter.ModuleDetailAdapterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ModuleDetailAdapterViewHolder {
        val binding = ItemModuleDetailBinding.inflate(LayoutInflater.from(parent.context))
        return ModuleDetailAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleDetailAdapterViewHolder, position: Int) =
        holder.bind(modules[position])

    override fun getItemCount() = modules.size

    inner class ModuleDetailAdapterViewHolder(val binding: ItemModuleDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(module: Module) {
            binding.tvModule.text = module.name
        }
    }
}