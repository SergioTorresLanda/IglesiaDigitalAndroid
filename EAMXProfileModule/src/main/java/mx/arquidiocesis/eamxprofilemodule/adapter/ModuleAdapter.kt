package mx.arquidiocesis.eamxprofilemodule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxprofilemodule.databinding.ItemModuleBinding
import mx.arquidiocesis.eamxprofilemodule.model.ModuleModel

class ModuleAdapter(val modules: ArrayList<ModuleModel>, val listener: (ModuleModel) -> Unit) :
    RecyclerView.Adapter<ModuleAdapter.ModuleAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleAdapterViewHolder {
        val binding = ItemModuleBinding.inflate(LayoutInflater.from(parent.context))
        return ModuleAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleAdapterViewHolder, position: Int) =
        holder.bind(modules[position], listener)

    override fun getItemCount() = modules.size

    inner class ModuleAdapterViewHolder(val binding: ItemModuleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(module: ModuleModel, listener: (ModuleModel) -> Unit) {

            binding.apply {
                tvModule.text = module.name
                root.setOnClickListener {
                    listener(module)
                }
            }
        }
    }
}