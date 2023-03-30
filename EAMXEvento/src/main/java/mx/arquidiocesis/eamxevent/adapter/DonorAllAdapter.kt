package mx.arquidiocesis.eamxevent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.databinding.ItemEventParticipantsDetailBinding
import mx.arquidiocesis.eamxevent.model.DonorResponse

class DonorAllAdapter(
    var context: Context,
    var List: ArrayList<DonorResponse>,
    private val recyclerView: RecyclerView,
) : RecyclerView.Adapter<DonorAllAdapter.NewsViewHolder>(), Filterable {
    var items: ArrayList<DonorResponse> = ArrayList()
    lateinit var onItemClickListener: (DonorResponse, String) -> Unit
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventParticipantsDetailBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class NewsViewHolder(private val binding: ItemEventParticipantsDetailBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(item: DonorResponse) = with(binding) {
            if (item.fIDONANTEID == null) {
                contenido.visibility = View.GONE
                tvVacioParticipants.visibility = View.VISIBLE
                tvVacioParticipants.text = "No se encontraron donadores."
            } else {
                tvNombre.text = item.fCNOMBRE
                tvCorreo.text = item.fCCORREO
                tvTelefono.text = item.fCTELEFONO
                tvLabelInformacion.visibility = View.GONE
                tvInformacion.text = item.fCCOMENTARIOS
                switchActivo.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        switchActivo.thumbTintList =
                            ContextCompat.getColorStateList(context, R.color.green_retirar)
                    } else {
                        switchActivo.thumbTintList =
                            ContextCompat.getColorStateList(context, R.color.hint_color)
                    }
                }
                switchActivo.isChecked = true
            }
        }
    }

    override fun getItemCount(): Int = items.size
    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }
}