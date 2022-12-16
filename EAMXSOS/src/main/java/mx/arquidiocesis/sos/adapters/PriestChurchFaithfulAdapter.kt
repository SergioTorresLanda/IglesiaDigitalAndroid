package  mx.arquidiocesis.sos.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.ItemChurchNearBinding
import mx.arquidiocesis.sos.model.LocationSOSModel
import mx.arquidiocesis.sos.model.SupportModel

class PriestChurchFaithfulAdapter(
    var list: List<LocationSOSModel>,
    val listenerLlamar: (String) -> Unit,
    val listener: (LocationSOSModel, SupportModel) -> Unit,
) : RecyclerView.Adapter<PriestChurchFaithfulAdapter.PriestChurchFaithfulHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PriestChurchFaithfulHolder(
        ItemChurchNearBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(
        holder: PriestChurchFaithfulAdapter.PriestChurchFaithfulHolder,
        position: Int
    ) {
        holder.binding.apply {
            list[position].let { location ->
                tvAssociation.text = location.name
                tvDistance.text = ivChurch.context.getString(
                    R.string.text_km_card,
                    String.format("%.2f", location.distance / 1000)
                )

                Glide.with(ivChurch)
                    .load(Uri.parse(location.image_url))
                    .error(R.drawable.ic_church)
                    .transform(CenterCrop(), RoundedCorners(24))
                    .into(ivChurch)

//                val currentDate = SimpleDateFormat("HH:mm:ss").format(Date())
                val validaTelefono = true
//                location.phone?.let { phone -> TODO Valida orarios
//                    try {
//                        if (phone != "None") {
//                            val horarioActual = currentDate.split(":")
//                            val horaActual = horarioActual[0].toInt()
//                                location.schedules.let {horarios->
//                                    horarios.forEach {
//                                        val horaInicioIglesia = it.start_hour.split(":")
//                                        if (horaActual > horaInicioIglesia[0].trim().toInt()) {
//                                            val horaFinIglesia = it.end_hour.split(":")
//                                            if (horaActual < horaFinIglesia[0].trim().toInt()) {
//                                                validaTelefono = true
//                                            }
//                                        }
//
//                                    }
//                                }
//
//                        }
//                    } catch (_: Exception) {
//                    }
//                }
                ivLlamada.setImageResource(if (validaTelefono) R.drawable.ic_llamada_telefonica else R.drawable.ic_none_llamada)
                cvPriest.setOnClickListener {
                    if (validaTelefono) listenerLlamar(location.phone!!)
                }
                rvPriests.adapter =
                    PriestOfChurchNearAdapter(location, location.support_contacts, listener)
            }
        }
    }

    override fun getItemCount() = list.size

    inner class PriestChurchFaithfulHolder(val binding: ItemChurchNearBinding) :
        RecyclerView.ViewHolder(binding.root)
}