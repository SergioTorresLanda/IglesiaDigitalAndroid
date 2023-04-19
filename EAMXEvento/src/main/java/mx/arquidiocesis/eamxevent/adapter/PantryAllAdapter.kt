package mx.arquidiocesis.eamxevent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxevent.databinding.ItemEventDetailPantryBinding
import mx.arquidiocesis.eamxevent.model.Pantry

class PantryAllAdapter(val context: Context,
                       val participation: Int = 0
) : RecyclerView.Adapter<PantryAllAdapter.NewsViewHolder>(), Filterable {

    var items: ArrayList<Pantry> = ArrayList()
    lateinit var onItemClickListener: (Pantry, String) -> Unit

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PantryAllAdapter.NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventDetailPantryBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val cadenaModel = items[position]
        holder.bind(cadenaModel)
    }

    inner class NewsViewHolder(private val binding: ItemEventDetailPantryBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(item: Pantry) = with(binding) {
            val userId = eamxcu_preferences.getData(
                EAMXEnumUser.USER_ID.name,
                EAMXTypeObject.INT_OBJECT
            ) as Int
            if (item.id == null) {
                tvVacio.visibility = View.VISIBLE
                lResponsabilityPantry.visibility = View.GONE
                lEmailPantry.visibility = View.GONE
                lPhonePantry.visibility = View.GONE
                lAddressPantry.visibility = View.GONE
                lZonePantry.visibility = View.GONE
                lDescriptionPantry.visibility = View.GONE
                lReqDonorPantry.visibility = View.GONE
                lScheduleReceivedPantry.visibility = View.GONE
                lDateReceived.visibility = View.GONE
                lScheduleReceived.visibility = View.GONE
                lDaysReceived.visibility = View.GONE
                lScheduleArmedPantry.visibility = View.GONE
                lDateArmed.visibility = View.GONE
                lScheduleArmed.visibility = View.GONE
                lDaysArmed.visibility = View.GONE
                lScheduleDeliveryPantry.visibility = View.GONE
                lDateDelivery.visibility = View.GONE
                lScheduleDelivery.visibility = View.GONE
                lDaysDelivery.visibility = View.GONE
                lAddressDelivery.visibility = View.GONE
            } else {
                tvVacio.visibility = View.GONE
                lResponsabilityPantry.visibility = View.VISIBLE
                tvResponsabilityPantry.text = item.responsability
                lEmailPantry.visibility = View.VISIBLE
                tvEmailPantry.text = item.email
                lPhonePantry.visibility = View.VISIBLE
                tvPhonePantry.text = item.phone
                lAddressPantry.visibility = View.VISIBLE
                tvAddressPantry.text = item.address
                lZonePantry.visibility = View.VISIBLE
                tvZonePantry.text = item.zone_id.toString()
                lDescriptionPantry.visibility = View.VISIBLE
                tvDescriptionPantry.text = item.description_requirements
                lReqDonorPantry.visibility = View.VISIBLE
                tvReqDonorPantry.text = item.requirements_donor

                //Recepción de despensas
                lScheduleReceivedPantry.visibility = View.VISIBLE
                lDateReceived.visibility = View.VISIBLE
                tvDateReceived.text = item.received!!.date_start + "-" + item.received!!.date_end
                lScheduleReceived.visibility = View.VISIBLE
                tvScheduleReceived.text = item.schedule!![0].hour_start + "-" + item.schedule!![0].hour_end
                val daysReception = item.schedule!![0].days!!.filter { it.checked }
                var diasRecepcion = ""
                var contReception = 1
                daysReception.forEach {
                    diasRecepcion =
                        diasRecepcion + (if (diasRecepcion == "") "" else (if (daysReception.size == contReception) " y " else ", ")) + it.name
                    contReception++
                }
                lDaysReceived.visibility = View.VISIBLE
                tvDaysReceived.text = diasRecepcion

                //Armado de despensas
                lScheduleArmedPantry.visibility = View.VISIBLE
                lDateArmed.visibility = View.VISIBLE
                tvDateArmed.text = item.armed!!.date_start + "-" + item.armed!!.date_end
                lScheduleArmed.visibility = View.VISIBLE
                tvScheduleArmed.text = item.schedule!![1].hour_start + "-" + item.schedule!![1].hour_end
                val daysArmed = item.schedule!![1].days!!.filter { it.checked }
                var diasArmado = ""
                var contArmed = 1
                daysArmed.forEach {
                    diasArmado =
                        diasArmado + (if (diasArmado == "") "" else (if (daysArmed.size == contArmed) " y " else ", ")) + it.name
                    contArmed++
                }
                lDaysArmed.visibility = View.VISIBLE
                tvDaysArmed.text = diasArmado

                //Entrega de despensas
                lScheduleDeliveryPantry.visibility = View.VISIBLE
                lDateDelivery.visibility = View.VISIBLE
                tvDateDelivery.text = item.delivery!!.date_start + "-" + item.delivery!!.date_end
                lScheduleDelivery.visibility = View.VISIBLE
                tvScheduleDelivery.text = item.schedule!![2].hour_start + "-" + item.schedule!![2].hour_end
                var daysDelivery = item.schedule!![2].days!!.filter { it.checked }
                var diasEntrega = ""
                var contDelivery = 1
                daysDelivery.forEach {
                    diasEntrega =
                        diasEntrega + (if (diasEntrega == "") "" else (if (daysDelivery.size == contDelivery) " y " else ", ")) + it.name
                    contDelivery++
                }
                lDaysDelivery.visibility = View.VISIBLE
                tvDaysDelivery.text = diasEntrega

                lAddressDelivery.visibility = View.VISIBLE
                tvAddressDelivery.text = item.address_delivery
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }


}