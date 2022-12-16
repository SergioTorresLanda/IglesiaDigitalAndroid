package mx.arquidiocesis.oraciones.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arquidiocesis.oraciones.databinding.ItemBusquedaOracionBinding
import mx.arquidiocesis.oraciones.model.OracionesModel

class TipoOracionesAdapter(
    val item: List<OracionesModel>?,
    val context: Context,
    val listener: (OracionesModel) -> Unit
) :
    RecyclerView.Adapter<TipoOracionesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBusquedaOracionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(context, listener, item?.get(position))

    override fun getItemCount(): Int = item!!.size

    class ViewHolder(val binding: ItemBusquedaOracionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            listener: (OracionesModel) -> Unit,
            church: OracionesModel?
        ) {
            binding.tvOracion.text = church?.name

            binding.root.setOnClickListener {
                if (church != null)
                    listener(church)
            }
        }
    }
}