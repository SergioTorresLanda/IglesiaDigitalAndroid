package mx.arquidiocesis.eamxevent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.databinding.ItemEventActorsDetailBinding
import mx.arquidiocesis.eamxevent.model.OtherActor

class ActorAllAdapter(
    var context: Context,
    var List: ArrayList<OtherActor>,
    private val recyclerView: RecyclerView,
) : RecyclerView.Adapter<ActorAllAdapter.NewsViewHolder>(), Filterable {
    var items: ArrayList<OtherActor> = ArrayList()
    lateinit var onItemClickListener: (OtherActor, String) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventActorsDetailBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class NewsViewHolder(private val binding: ItemEventActorsDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OtherActor) = with(binding) {
            contenido.visibility=View.VISIBLE
            tvNombre.text = item.nombre
            tvCorreo.text = item.correo
            tvTelefono.text = item.telefono
            tvLabelInformacion.visibility = View.GONE
            tvInformacion.text = item.comentarios
            switchActivo.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    switchActivo.thumbTintList = ContextCompat.getColorStateList(context, R.color.green_retirar)
                } else {
                    switchActivo.thumbTintList = ContextCompat.getColorStateList(context, R.color.hint_color)
                }
            }
            switchActivo.isChecked = true
        }
    }

    override fun getItemCount(): Int = items.size
    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }
}