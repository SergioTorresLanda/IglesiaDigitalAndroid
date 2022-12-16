package mx.arquidiocesis.servicios.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.servicios.databinding.ItemSacramentBinding
import mx.arquidiocesis.servicios.model.SacramentsModel

class SacramentsAdapter(
    var list: List<SacramentsModel>,
    val listener: (SacramentsModel) -> Unit
) :
    RecyclerView.Adapter<SacramentsAdapter.ServicesViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServicesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSacramentBinding.inflate(inflater, parent, false)
        return ServicesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) =
        holder.bind(listener, list[position])

    override fun getItemCount(): Int = list.size

    class ServicesViewHolder(val binding: ItemSacramentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: (SacramentsModel) -> Unit, sacrament: SacramentsModel) {
            binding.tvName.text = sacrament.name

            binding.root.setOnClickListener {
                listener(sacrament)
            }
        }
    }
}