package mx.arquidiocesis.oraciones.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arquidiocesis.oraciones.databinding.ItemOracionesBinding
import mx.arquidiocesis.oraciones.model.OracionesModel


class OracionDetalleAdapter(
    var oraciones: List<OracionesModel>,
    var context: Context,
    val listener: (OracionesModel) -> Unit, ) :
    RecyclerView.Adapter<OracionDetalleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = ItemOracionesBinding.inflate(inflater, parent, false)
        return OracionDetalleAdapter.ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(oraciones[position], listener, context)

    override fun getItemCount(): Int = oraciones.size

    class ViewHolder(val binding: ItemOracionesBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(oraciones: OracionesModel, listener: (OracionesModel) -> Unit, context: Context) {

            binding.tvOracion.text = oraciones.name
            binding.cvOraciones.setOnClickListener {
                listener(oraciones)


            }
        }
    }
}
