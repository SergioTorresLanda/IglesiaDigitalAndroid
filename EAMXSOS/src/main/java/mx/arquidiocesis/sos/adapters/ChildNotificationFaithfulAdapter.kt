package  mx.arquidiocesis.sos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.ItemNotificationFaithfulBinding
import mx.arquidiocesis.sos.model.ServicesPriestModel
import mx.arquidiocesis.sos.utils.Constants

class ChildNotificationFaithfulAdapter(
    var servicesPriest: List<ServicesPriestModel>,
    val listener: (ServicesPriestModel) -> Unit,
) : RecyclerView.Adapter<ChildNotificationFaithfulAdapter.ItemNotificationFaithfulHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildNotificationFaithfulAdapter.ItemNotificationFaithfulHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotificationFaithfulBinding.inflate(inflater, parent, false)
        return ItemNotificationFaithfulHolder(binding)
    }

    override fun onBindViewHolder(
        holderItem: ChildNotificationFaithfulAdapter.ItemNotificationFaithfulHolder,
        position: Int
    ) {
        holderItem.bind(listener, servicesPriest[position])
    }

    override fun getItemCount(): Int = servicesPriest.size

    inner class ItemNotificationFaithfulHolder(val binding: ItemNotificationFaithfulBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listener: (ServicesPriestModel) -> Unit,
            servicesPriest: ServicesPriestModel
        ) {
            binding.apply {
                tvNamePriestFaithfulSOS.text = servicesPriest.devotee.name
                tvServiceFaithfulSOS.text = servicesPriest.service.name
                if (!servicesPriest.sub_status.isNullOrEmpty()) {
                    when (servicesPriest.sub_status) {
                        Constants.SubStatus.PENDING_CONFIRMATION -> {
                            tvStatusFaithfulSOS.text = "Por aceptar"
                        }
                        Constants.SubStatus.CALL_WAITING -> {
                            tvStatusFaithfulSOS.text = "Aceptada  \n" +
                                    "Llamada en espera"
                        }
                        Constants.SubStatus.CALL_FINISHED -> {
                            tvStatusFaithfulSOS.text = "Aceptada  \n" +
                                    "Llamada realizada"
                        }
                        Constants.SubStatus.LOOKING_FOR_ASSISTANCE -> {
                            tvStatusFaithfulSOS.text = "Si se realizará el servicio"
                        }
                        Constants.SubStatus.HELP_ON_THE_WAY -> {
                            tvStatusFaithfulSOS.text = "Ayuda en camino"

                        }
                        Constants.SubStatus.SUCCESSFULLY -> {
                            tvStatusFaithfulSOS.text = "Finalizado"
                        }
                        Constants.SubStatus.UNSUCCESSFULLY -> {
                            tvStatusFaithfulSOS.text = "Finalizado"

                        }
                    }
                } else {
                    if (Constants.SubStatus.CANCELLED == servicesPriest.status) {
                        tvStatusFaithfulSOS.text = "Cancelado"
                    } else {
                        tvStatusFaithfulSOS.text = servicesPriest.status
                    }
                }

                clSwipe.setOnClickListener { listener(servicesPriest) }
            }
        }
    }
}