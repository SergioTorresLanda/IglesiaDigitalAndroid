package mx.arquidiocesis.eamxevent.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.protobuf.Empty
import kotlinx.android.synthetic.main.item_event_detail.view.*
import mx.arquidiocesis.eamxcommonutils.base.FragmentBase
import mx.arquidiocesis.eamxcommonutils.common.EAMXBaseFragment
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.navigation.NavigationFragment
import mx.arquidiocesis.eamxevent.R
import mx.arquidiocesis.eamxevent.databinding.ItemEventDetailBinding
import mx.arquidiocesis.eamxevent.model.DinerResponse
import mx.arquidiocesis.eamxevent.model.DonorResponse
import mx.arquidiocesis.eamxevent.model.VolunteerResponse
import mx.arquidiocesis.eamxevent.model.enum.Participation
import mx.arquidiocesis.eamxevent.ui.DONAR
import mx.arquidiocesis.eamxevent.ui.EDITAR
import mx.arquidiocesis.eamxevent.ui.EventDonorFragment
import mx.arquidiocesis.eamxevent.ui.PARTICIPAR
import java.util.regex.Pattern

class VolunteerAllAdapter(
    val context: Context
) : RecyclerView.Adapter<VolunteerAllAdapter.NewsViewHolder>(), Filterable{
    var items: ArrayList<VolunteerResponse> = ArrayList()
    lateinit var onItemClickListener: (VolunteerResponse, String) -> Unit
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventDetailBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(items[position])
    }
    inner class NewsViewHolder(private val binding: ItemEventDetailBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(item: VolunteerResponse) = with(binding) {
            item.fCCORREO
            //item.fCDIRECCION
            item.fCFECALTA
            item.fCMULTIUSER
            item.fCNOMBRECOM //id_comedor
            //item.fCRESPONSABLE
            item.fCTELEFONO
            item.fCVOLUNTARIO
            item.fIVOLUNTARIOID
            btnOpciones.setOnClickListener {
                onItemClickListener(item, EDITAR)
            }
        }
    }
    override fun getItemCount(): Int = items.size
    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }
}