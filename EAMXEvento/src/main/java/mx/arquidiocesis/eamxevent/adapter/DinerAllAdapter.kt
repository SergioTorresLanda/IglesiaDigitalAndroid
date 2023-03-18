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
import mx.arquidiocesis.eamxevent.model.DinerResponse
import mx.arquidiocesis.eamxevent.ui.EDITAR
import java.util.regex.Pattern

class DinerAllAdapter(
    val context: Context,
    val isSuper: Boolean = false,
    val isPrincipal: Boolean = true
) : RecyclerView.Adapter<DinerAllAdapter.NewsViewHolder>(), Filterable {

    var items: ArrayList<DinerResponse> = ArrayList()
    //var dinerListFilter: ArrayList<DinerResponse> = ArrayList()
    lateinit var onItemClickListener: (DinerResponse, String) -> Unit


    /*expresión regular*/
    var URL_REGEX =
        "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" + "(%{2}|[-()_.!~*';/?:@&=+$, A-Za-z0-9])+)" + "([).!';/?:, ][[:blank:]])?$"
    var URL_PATTERN = Pattern.compile(URL_REGEX)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventDetailBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val cadenaModel = items[position]
        holder.bind(cadenaModel)
    }

    inner class NewsViewHolder(private val binding: ItemEventDetailBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(item: DinerResponse) = with(binding) {
            val userId = eamxcu_preferences.getData(
                EAMXEnumUser.USER_ID.name,
                EAMXTypeObject.INT_OBJECT
            ) as Int

            if (item.fCCOMEDORID == null) {
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
                tvVacio.visibility = View.GONE
                tvNombreF.text = item.fCNOMBRECOM
                lNombreF.visibility = View.VISIBLE
                tvCorreoF.text = item.fCCORREO
                lCorreoF.visibility = View.VISIBLE
                tvRequisF.text = item.fCREQUISITOS
                lRequisF.visibility = View.VISIBLE
                lDiasF.visibility = View.VISIBLE
                var dias = ""
                item.fCHORARIOS!![0].days!!.forEach {
                    if(it.checked){
                        dias = dias + (if (dias=="") "" else ", ")+it.name
                    }
                }
                tvDiasF.text = dias
                lHorarioF.visibility = View.VISIBLE
                tvHorarioF.text = item.fCHORARIOS!![0].hour_start+"-"+item.fCHORARIOS!![0].hour_end
                tvDireccionF.text = item.fCDIRECCION
                lDireccionF.visibility = View.VISIBLE
                tvCorreoF.text = item.fCCORREO
                lCorreoF.visibility = View.VISIBLE
                tvPrecioF.text = item.fCCOBRO
                lPrecioF.visibility = View.VISIBLE
                tvTelF.text = item.fCTELEFONO
                lTelF.visibility = View.VISIBLE
                tvResponF.text = item.fCRESPONSABLE
                lResponF.visibility = View.VISIBLE
                if(userId.toString() == item.fIUSERID) {
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