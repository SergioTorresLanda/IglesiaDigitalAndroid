package mx.arquidiocesis.oraciones.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.oraciones.model.OracionesModel
import com.arquidiocesis.oraciones.databinding.ItemRowOracionBinding


class OracionAdapter(
    var oraciones: List<OracionesModel>,
    var context: Context,
    val listener: (OracionesModel) -> Unit, ) :
    RecyclerView.Adapter<OracionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowOracionBinding.inflate(inflater, parent, false)
        return OracionAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(oraciones[position], listener, context)

    override fun getItemCount(): Int = oraciones.size

    class ViewHolder(val binding: ItemRowOracionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(oraciones: OracionesModel, listener: (OracionesModel) -> Unit, context: Context) {

            binding.tvOracion.text = oraciones.name
            binding.cvOraciones.setOnClickListener {
                listener(oraciones)
            }


        }
    }
}