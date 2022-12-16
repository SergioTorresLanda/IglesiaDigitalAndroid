package mx.arquidiocesis.servicios.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.servicios.databinding.ItemIglesiasServiciosBinding
import mx.arquidiocesis.servicios.model.IglesiasModel

class MassesAdapter(
    var oraciones: MutableList<IglesiasModel>,
    var context: Context,
    var recyclerView: RecyclerView,
    val listener: (IglesiasModel) -> Unit
) :
    RecyclerView.Adapter<MassesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemIglesiasServiciosBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(oraciones[position], context, listener)

    override fun getItemCount(): Int = oraciones.size


    fun updateList(item: MutableList<IglesiasModel>) {
        oraciones = item
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    class ViewHolder(val view: ItemIglesiasServiciosBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(
            masse: IglesiasModel,
            context: Context,
            listener: (IglesiasModel) -> Unit
        ) {
            view.tvTitulo.text = masse.name
            Glide.with(context)
                .load(Uri.parse(masse.image_url))
                .centerCrop()
                .into(view.ivMisas)


            view.tvKilometros.text = "${masse.distance} km"
            view.cvMisas.setOnClickListener {
                listener(masse)
            }
        }
    }
}