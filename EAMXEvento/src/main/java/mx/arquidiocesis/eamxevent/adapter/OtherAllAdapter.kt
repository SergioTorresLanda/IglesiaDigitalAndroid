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
import mx.arquidiocesis.eamxevent.databinding.ItemEventDetailBinding
import mx.arquidiocesis.eamxevent.databinding.ItemEventOtherBinding
import mx.arquidiocesis.eamxevent.model.OtherEvent
import mx.arquidiocesis.eamxevent.model.enum.TypeActivity
import mx.arquidiocesis.eamxevent.ui.AYUDAR
import mx.arquidiocesis.eamxevent.ui.DONAR
import mx.arquidiocesis.eamxevent.ui.EDITAR
import mx.arquidiocesis.eamxevent.ui.PARTICIPAR
import java.util.regex.Pattern

class OtherAllAdapter(
    val context: Context,
    val participation: Int = 0
) : RecyclerView.Adapter<OtherAllAdapter.NewsViewHolder>(), Filterable{

    var items: ArrayList<OtherEvent> = ArrayList()
    //var dinerListFilter: ArrayList<DinerResponse> = ArrayList()
    lateinit var onItemClickListener: (OtherEvent, String) -> Unit

    /*expresión regular*/
    var URL_REGEX =
        "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" + "(%{2}|[-()_.!~*';/?:@&=+$, A-Za-z0-9])+)" + "([).!';/?:, ][[:blank:]])?$"
    var URL_PATTERN = Pattern.compile(URL_REGEX)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventOtherBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val cadenaModel = items[position]
        holder.bind(cadenaModel)
    }

    inner class NewsViewHolder(private val binding: ItemEventOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OtherEvent) = with(binding) {
            val userId = eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int
            val types: Array<TypeActivity> = TypeActivity.values()

            if (item.eventoId == null) {
                tvVacio.visibility = View.VISIBLE
                tvNombre.visibility = View.GONE
                lNombreF.visibility = View.GONE
                lCorreoF.visibility = View.GONE
                lDescF.visibility = View.GONE
                lDireccionF.visibility = View.GONE
                lCorreoF.visibility = View.GONE
                lPrecioF.visibility = View.GONE
                lTelF.visibility = View.GONE
                lResponF.visibility = View.GONE
                lDiasF.visibility = View.GONE
                lHorarioF.visibility = View.GONE
                lPublicF.visibility = View.GONE
                lDonsF.visibility = View.GONE
                lVolsF.visibility = View.GONE
                viewBottom.visibility =View.GONE

            } else {
                tvVacio.visibility = View.GONE
                lNombreF.visibility = View.VISIBLE
                tvNombre.visibility = View.VISIBLE
                lCorreoF.visibility = View.VISIBLE
                lDescF.visibility = View.VISIBLE
                lDiasF.visibility = View.VISIBLE
                lPrecioF.visibility = View.VISIBLE
                lHorarioF.visibility = View.VISIBLE
                lTelF.visibility = View.VISIBLE
                lDireccionF.visibility = View.VISIBLE
                lResponF.visibility = View.VISIBLE
                lPublicF.visibility = View.VISIBLE
                lDonsF.visibility = View.VISIBLE
                lVolsF.visibility = View.VISIBLE
                viewBottom.visibility =View.VISIBLE

                tvTipoF.text= types[item.tipoEvento!!].type
                tvNombre.text = item.nombre
                tvPrecioF.text = item.cobro.toString()
                tvHorarioF.text = "De "+item.horarios!![0].hour_start + " a " + item.horarios!![0].hour_end + " hrs."
                tvTelF.text = item.telefono
                tvCorreoF.text = item.correo
                tvResponF.text = item.responsable

                val days = item.horarios[0].days!!.filter { it.checked }
                var dias = ""
                var cont = 1
                days.forEach {
                    dias = dias + (if (dias == "") "" else (if (days.size == cont) " y " else ", ")) + it.name
                    cont++
                }
                tvDiasF.text = dias
                tvDireccionF.text = item.direccion
                tvRequisF.text = item.descripcion
                tvPublicF.text = item.publico
                tvDonsF.text = item.donantesTxt
                tvVolsF.text = item.voluntariosTxt
                var accion = ""
                if (userId == item.userId) {
                    btnOpciones.visibility = View.GONE
                    cwxEvent.setOnClickListener {
                        onItemClickListener(item, EDITAR)
                    }
                }else{
                    btnOpciones.visibility = View.VISIBLE
                    when(participation){
                        1 -> {
                            accion = DONAR
                            btnOpciones.text = "Donar"
                        }
                        2 -> {
                            accion = AYUDAR
                            btnOpciones.text = "Ayudar"
                        }
                        3 -> {
                            accion = PARTICIPAR
                            btnOpciones.text = "Participar"
                        }else ->{
                            btnOpciones.visibility = View.GONE
                            accion = ""
                        }
                    }
                    if (accion.isNotEmpty())
                        btnOpciones.setOnClickListener {
                            onItemClickListener(item, accion)
                        }
                }

            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }

}