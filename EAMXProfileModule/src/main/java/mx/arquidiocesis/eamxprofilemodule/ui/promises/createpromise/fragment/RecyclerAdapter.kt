package mx.arquidiocesis.eamxprofilemodule.ui.promises.createpromise.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxprofilemodule.databinding.ModeloRowBinding
import mx.arquidiocesis.eamxprofilemodule.ui.promises.createpromise.modelo.Model


class RecyclerAdapter : ListAdapter<Model, RecyclerAdapter.CadenaViewHolder>(EAMXDiffCallback) {

    lateinit var onItemClickListener: (Model) -> Unit
    lateinit var onItemClickPlaying: (Model) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CadenaViewHolder {
        val binding = ModeloRowBinding.inflate(LayoutInflater.from(parent.context))
        return CadenaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CadenaViewHolder, position: Int) {
        val cadenaModel = getItem(position)
        holder.bind(cadenaModel)
    }

    inner class CadenaViewHolder(private val binding: ModeloRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cadenaModel: Model) = with(binding) {
            binding.textList.text = cadenaModel.promisseDescription

        }
    }
}
