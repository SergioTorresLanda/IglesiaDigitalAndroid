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
import mx.arquidiocesis.eamxevent.databinding.ItemDonorDetailBinding
import mx.arquidiocesis.eamxevent.databinding.ItemEventDetailBinding
import mx.arquidiocesis.eamxevent.model.DinerResponse
import mx.arquidiocesis.eamxevent.model.DonorResponse
import mx.arquidiocesis.eamxevent.ui.EDITAR
import java.util.regex.Pattern

class DonorAllAdapter (val context: Context,
                       val isSuper: Boolean = false,
                       val isPrincipal: Boolean = true,
) : RecyclerView.Adapter<DonorAllAdapter.NewsViewHolder>(), Filterable {

    var items: ArrayList<DonorResponse> = ArrayList()

    //var dinerListFilter: ArrayList<DinerResponse> = ArrayList()
    lateinit var onItemClickListener: (DonorResponse, String) -> Unit


    /*expresión regular*/
    var URL_REGEX =
        "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" + "(%{2}|[-()_.!~*';/?:@&=+$, A-Za-z0-9])+)" + "([).!';/?:, ][[:blank:]])?$"
    var URL_PATTERN = Pattern.compile(URL_REGEX)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDonorDetailBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val cadenaModel = items[position]
        holder.bind(cadenaModel)
    }

    inner class NewsViewHolder(private val binding: ItemDonorDetailBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(item: DonorResponse) = with(binding) {
            val userId = eamxcu_preferences.getData(
                EAMXEnumUser.USER_ID.name,
                EAMXTypeObject.INT_OBJECT
            ) as Int

            if (item.fICOMEDORID == null) {
                tvVacio.visibility = View.VISIBLE
                lNombreF.visibility = View.GONE
                lCorreoF.visibility = View.GONE
                lRequisF.visibility = View.GONE
                lDireccionF.visibility = View.GONE
                lCorreoF.visibility = View.GONE
                lPrecioF.visibility = View.GONE
                lTelF.visibility = View.GONE
                lResponF.visibility = View.GONE
                lDiasF.visibility = View.GONE
                lHorarioF.visibility = View.GONE
            } else {
                //nombre
                tvVacio.visibility = View.GONE
                tvNombreF.text = item.fCNOMBRE
                lNombreF.visibility = View.VISIBLE
                //correo
                tvCorreoF.text = item.fCCORREO
                lCorreoF.visibility = View.VISIBLE
                //comentarios
                tvRequisF.text = item.fCCOMENTARIOS
                lRequisF.visibility = View.VISIBLE
                lDiasF.visibility = View.VISIBLE
                /*
                var days = item.fCHORARIOS!![0].days!!.filter { it.checked }
                var dias = ""
                var cont = 1
                days.forEach {
                    dias =
                        dias + (if (dias == "") "" else (if (days.size == cont) " y " else ", ")) + it.name
                    cont++
                }

                 */
                //fecha alta
                tvDiasF.text = item.fCFECALTA
                //tipo donacion
                lHorarioF.visibility = View.VISIBLE
                tvHorarioF.text = item.fCTIPODONA


                //bancarios
                tvDireccionF.text = item.fCBANCARIOS
                lDireccionF.visibility = View.VISIBLE
                //id comedor
                tvPrecioF.text = item.fICOMEDORID
                lPrecioF.visibility = View.VISIBLE
                //telefono
                tvTelF.text = item.fCTELEFONO
                lTelF.visibility = View.VISIBLE
                // id donante
                tvResponF.text = item.fIDONANTEID
                lResponF.visibility = View.VISIBLE
                // id usuario
                if (userId.toString() == item.fIUSERID) {
                    cwcEvent.setOnClickListener {
                        onItemClickListener(item, EDITAR)
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