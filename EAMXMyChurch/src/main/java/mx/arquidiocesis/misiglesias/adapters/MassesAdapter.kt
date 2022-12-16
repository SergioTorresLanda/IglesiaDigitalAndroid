package mx.arquidiocesis.misiglesias.adapters

import android.content.Context
import android.location.Location
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.misiglesias.databinding.ItemMisasBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import mx.arquidiocesis.misiglesias.model.MassesModelM

class MassesAdapter(
    var oraciones: MutableList<MassesModelM>,
    var context: Context,
    var recyclerView: RecyclerView,
    val listener: (MassesModelM) -> Unit
) :
    RecyclerView.Adapter<MassesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMisasBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(oraciones[position], context, listener)

    override fun getItemCount(): Int = oraciones.size


    fun updateList(item: MutableList<MassesModelM>) {
        oraciones = item
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    class ViewHolder(val view: ItemMisasBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(
            masse: MassesModelM,
            context: Context,
            listener: (MassesModelM) -> Unit
        ) {
            view.tvTitulo.text = masse.name
            Glide.with(context)
                .load(Uri.parse(masse.imageUrl))
                .apply(RequestOptions().override(250, 320).centerCrop())
                .into(view.ivMisas)
            val locationIglesia = Location("location")


            view.tvKilometros.text = "${masse.distance} km"
            view.tvHorarioss.text =  "${masse.schedules} hrs"
            view.cvMisas.setOnClickListener {
                listener(masse)
            }
        }
    }
}