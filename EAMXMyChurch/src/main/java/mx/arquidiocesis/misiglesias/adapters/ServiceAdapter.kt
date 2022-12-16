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
            if (!service.schedules.isNullOrEmpty()) {
                val item = service.schedules.first()
                binding.tvDia.text = PublicFunctions().obtenerDias(item.days)
                if (!item.hour_start.isNullOrEmpty()) {
                    binding.tvHorario.text = "${item.hour_start}  "
                }
               /* if (!item.hour_end.isNullOrEmpty()) {
                    binding.tvHorario.text = "${binding.tvHorario.text} ${item.hour_end} "
                }*/
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
            binding.tvDir.text = service.geared_toward
            if (edit) {
                binding.ivRemove.visibility = View.VISIBLE
            }
            binding.ivRemove.setOnClickListener {
                adapter.deleteReceiptsList(service)
            }
            /* Glide.with(context)
                 .load(Uri.parse(service.icon)).apply(RequestOptions().override(40, 50))
                 .into(binding.ivIcon)
              */   /**/

            /*              binding.imClose.setOnClickListener {
                              adapter.deleteReceiptsList(service)
                          }

                          binding.ivEditDays.setOnClickListener {
                              val days =
                                  mutableListOf(
                                      "Lunes",
                                      "Martes",
                                      "Miercoles",
                                      "Jueves",
                                      "Viernes",
                                      "Sabado",
                                      "Domingo"
                                  )
                              PublicFunctions().selectDayRange(context, binding.tvDia, days, "")
                          }

                          binding.ivEditHours.setOnClickListener {
                              PublicFunctions().selectFirstHour(context, binding.tvHorario, "")
                          }
          */


        }


    }
}