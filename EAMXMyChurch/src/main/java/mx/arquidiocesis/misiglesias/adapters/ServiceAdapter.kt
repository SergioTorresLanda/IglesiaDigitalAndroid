package mx.arquidiocesis.misiglesias.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.misiglesias.databinding.ItemServiceScheduleBinding
import mx.arquidiocesis.misiglesias.model.ServiceModel
import mx.arquidiocesis.misiglesias.utils.PublicFunctions

class ServiceAdapter(
    private val edit: Boolean = false,
    var services: MutableList<ServiceModel>,
    var recyclerView: RecyclerView,
    val listener: (ServiceModel) -> Unit
) :
    RecyclerView.Adapter<ServiceAdapter.ServicesViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServicesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServiceScheduleBinding.inflate(inflater, parent, false)
        return ServicesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) =
        holder.bind(edit, listener, services[position], this)

    override fun getItemCount(): Int = services.size
    fun updateReceiptsList(item: ServiceModel) {
        services.add(item)
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    fun updateReceiptsList(item: MutableList<ServiceModel>) {
        services = item
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    fun deleteReceiptsList(item: ServiceModel) {
        services.remove(item)
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    class ServicesViewHolder(val binding: ItemServiceScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            edit: Boolean,
            listener: (ServiceModel) -> Unit,
            service: ServiceModel,
            adapter: ServiceAdapter
        ) {
            binding.tvServicio.text = service.type.name
            binding.tvHorario.visibility=View.GONE
            if (!service.schedules.isNullOrEmpty()) {
                for (item in service.schedules){
                    val dia = PublicFunctions().obtenerDias(item.days)
                    var horaIni = "00:00"
                    var horaFin = "00:00"
                    if (!item.hour_start.isNullOrEmpty()) {
                        horaIni=item.hour_start
                    }
                    if (!item.hour_end.isNullOrEmpty()) {
                        horaFin=item.hour_end
                    }
                    if (horaIni=="00:00" && horaFin=="00:00") {
                        binding.tvDia.append("${dia}: Horario flexible.\n")
                    }else{
                        binding.tvDia.append("${dia}: ${item.hour_start} a ${item.hour_end}\n")
                    }
                }
            }
            service.geared_toward.isNullOrEmpty().let {
                if (!it) {
                    binding.tvDir.visibility = View.VISIBLE
                }
            }
            service.geared_toward.isNullOrEmpty().let {
                if (!it) {
                    binding.tvDes.visibility = View.VISIBLE
                }
            }
            binding.tvDes.text = service.description
            binding.tvDir.text = "Dirigido a: ${service.geared_toward}"
            if (edit) {
                binding.ivRemove.visibility = View.VISIBLE
            }
            binding.ivRemove.setOnClickListener {
                adapter.deleteReceiptsList(service)
            }
        }
    }
}