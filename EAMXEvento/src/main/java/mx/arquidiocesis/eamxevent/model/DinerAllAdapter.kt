package mx.arquidiocesis.eamxevent.model

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcommonutils.util.EAMXFormatDate
import mx.arquidiocesis.eamxcommonutils.util.buildTextSuccess
import mx.arquidiocesis.eamxcommonutils.util.loadByUrl
import mx.arquidiocesis.eamxevent.databinding.ItemEventBinding
import java.util.regex.Pattern

class DinerAllAdapter(
    val context: Context,
    val isSuper: Boolean = false,
    val isPrincipal: Boolean = true
) : RecyclerView.Adapter<DinerAllAdapter.NewsViewHolder>(), Filterable {

    var items: ArrayList<DinerResponse> = ArrayList()
    //var dinerListFilter: ArrayList<DinerResponse> = ArrayList()


    /*expresión regular*/
    var URL_REGEX =
        "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" + "(%{2}|[-()_.!~*';/?:@&=+$, A-Za-z0-9])+)" + "([).!';/?:, ][[:blank:]])?$"
    var URL_PATTERN = Pattern.compile(URL_REGEX)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DinerAllAdapter.NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DinerAllAdapter.NewsViewHolder, position: Int) {
        val cadenaModel = items[position]
        holder.bind(cadenaModel)
    }

    inner class NewsViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(item :DinerResponse)
            = with(binding) {
            //var isMy = false
            tvNombreF.text = item.fCCOMEDORID
            tvCorreoF.text = item.fCCORREO
            tvRequisF.text = item.fCREQUISITOS
            tvHorarioF.text = item.fCHORARIOS
            tvDireccionF.text = item.fCDIRECCION
            tvCorreoF.text = item.fCCORREO
            tvPrecioF.text = item.fCCOBRO
            tvTelF.text = item.fCTELEFONO
        }

    }

    override fun getItemCount(): Int = items.size

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }


}