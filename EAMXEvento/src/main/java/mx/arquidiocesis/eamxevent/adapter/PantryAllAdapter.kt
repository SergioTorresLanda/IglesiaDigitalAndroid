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

class PantryAllAdapter(
    val context: Context,
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
            if (item.id == null) {
                tvVacio.visibility = View.VISIBLE
                lResponsabilityPantry.visibility = View.GONE
                lEmailPantry.visibility = View.GONE
                lPhonePantry.visibility = View.GONE
                lAddressPantry.visibility = View.GONE
                lDescriptionPantry.visibility = View.GONE
                lReqDonorPantry.visibility = View.GONE
                llDaysReceivePantry.visibility = View.GONE
                lScheduleReceivedPantry.visibility = View.GONE
                lDateReceived.visibility = View.GONE
                lScheduleReceived.visibility = View.GONE
                lScheduleArmedPantry.visibility = View.GONE
                lDateArmed.visibility = View.GONE
                lScheduleArmed.visibility = View.GONE
                lScheduleDeliveryPantry.visibility = View.GONE
                lDateDelivery.visibility = View.GONE
                lScheduleDelivery.visibility = View.GONE
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
                if (item.description_pantry == "None") {
                    lDescriptionPantry.visibility = View.GONE
                } else {
                    lDescriptionPantry.visibility = View.VISIBLE
                    tvDescriptionPantry.text = item.description_pantry
                }
                //Recepción de despensas
                if (item.required_donor == 0 && item.received == null) {
                    lScheduleReceivedPantry.visibility = View.GONE
                    lReqDonorPantry.visibility = View.GONE
                    llDaysReceivePantry.visibility = View.GONE
                    lDateReceived.visibility = View.GONE
                    lScheduleReceived.visibility = View.GONE
                } else {
                    lScheduleReceivedPantry.visibility = View.VISIBLE
                    lReqDonorPantry.visibility = View.VISIBLE
                    tvReqDonorPantry.text = item.donation_requirements
                    llDaysReceivePantry.visibility = View.VISIBLE
                    lDateReceived.visibility = View.VISIBLE
                    val daysReception = item.schedule!![0].days!!.filter { it.checked }
                    var diasRecepcion = ""
                    var contReception = 1
                    daysReception.forEach {
                        diasRecepcion =
                            diasRecepcion + (if (diasRecepcion == "") "" else (if (daysReception.size == contReception) " y " else ", ")) + it.name
                        contReception++
                    }
                    tvSchedulesReceived.text =
                        "Del " + item.received!!.date_start.toString() +
                                " al " + item.received.date_end.toString() + ". Los días: " + diasRecepcion
                    lScheduleReceived.visibility = View.VISIBLE
                    tvScheduleReceived.text =
                        "En un horario de " + item.received.hour_start + " - " + item.received.hour_end
                }
                //Armado de despensas
                if (item.required_armed == 0 && item.armed == null) {
                    lScheduleArmedPantry.visibility = View.GONE
                    lDateArmed.visibility = View.GONE
                    lScheduleArmed.visibility = View.GONE
                } else {
                    lScheduleArmedPantry.visibility = View.VISIBLE
                    lDateArmed.visibility = View.VISIBLE
                    val daysArmed = item.schedule!![1].days!!.filter { it.checked }
                    var diasArmado = ""
                    var contArmed = 1
                    daysArmed.forEach {
                        diasArmado =
                            diasArmado + (if (diasArmado == "") "" else (if (daysArmed.size == contArmed) " y " else ", ")) + it.name
                        contArmed++
                    }
                    tvSchedulesArmed.text =
                        "Del " + item.armed!!.date_start.toString() +
                                " al " + item.armed!!.date_end.toString() + ". Los días: " + diasArmado
                    lScheduleArmed.visibility = View.VISIBLE
                    tvScheduleArmed.text =
                        "En un horario de " + item.armed.hour_start + " - " + item.armed.hour_end
                }
                //Entrega de despensas
                if (item.required_delivery == 0 && item.delivery == null) {
                    lScheduleDeliveryPantry.visibility = View.GONE
                    lDateDelivery.visibility = View.GONE
                    lScheduleDelivery.visibility = View.GONE
                    lAddressDelivery.visibility = View.GONE
                } else {
                    lScheduleDeliveryPantry.visibility = View.VISIBLE
                    lDateDelivery.visibility = View.VISIBLE
                    var daysDelivery = item.schedule!![2].days!!.filter { it.checked }
                    var diasEntrega = ""
                    var contDelivery = 1
                    daysDelivery.forEach {
                        diasEntrega =
                            diasEntrega + (if (diasEntrega == "") "" else (if (daysDelivery.size == contDelivery) " y " else ", ")) + it.name
                        contDelivery++
                    }
                    tvSchedulesDelivery.text =
                        "Del " + item.delivery!!.date_start.toString() +
                        " al " + item.delivery!!.date_end.toString() + ". Los días: " + diasEntrega
                    lScheduleDelivery.visibility = View.VISIBLE
                    tvScheduleDelivery.text =
                        "En un horario de " + item.delivery!!.hour_start + " - " + item.delivery!!.hour_end
                    lAddressDelivery.visibility = View.VISIBLE
                    tvAddressDelivery.text = "A la dirección: " + item.address_delivery
                }
            }
        }
    }
    fun getMonthName(month: Int): String {
        return when(month) {
            1 -> "enero"
            2 -> "febrero"
            3 -> "marzo"
            4 -> "abril,"
            5 -> "mayo"
            6 -> "junio"
            7 -> "julio"
            8 -> "agosto"
            9 -> "septiembre"
            10 -> "octubre"
            11 -> "noviembre"
            12 -> "diciembre"
            else -> ""
        }
        val fecha = "23/03/2023"
        getMonthName(fecha.toInt())
    }

    override fun getItemCount(): Int = items.size

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }


}