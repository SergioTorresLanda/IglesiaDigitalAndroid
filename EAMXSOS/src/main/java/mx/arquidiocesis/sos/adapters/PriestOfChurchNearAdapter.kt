package  mx.arquidiocesis.sos.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upax.eamxsos.databinding.ItemContactoApoyoBinding
import mx.arquidiocesis.sos.model.LocationSOSModel
import mx.arquidiocesis.sos.model.SupportModel

class PriestOfChurchNearAdapter(val location : LocationSOSModel,
                                val list : List<SupportModel>?,
                                val listener: (LocationSOSModel, SupportModel) -> Unit,
) :
    RecyclerView.Adapter<PriestOfChurchNearAdapter.PriestOfChurchNearHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PriestOfChurchNearAdapter.PriestOfChurchNearHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContactoApoyoBinding.inflate(inflater, parent, false)
        return PriestOfChurchNearHolder(binding)
    }

    override fun onBindViewHolder(
        holderOf: PriestOfChurchNearAdapter.PriestOfChurchNearHolder,
        position: Int
    ) {
        holderOf.bind(listener, list?.get(position))
    }

    override fun getItemCount(): Int = list?.size ?: 0

    inner class PriestOfChurchNearHolder(val binding: ItemContactoApoyoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listener: (LocationSOSModel, SupportModel) -> Unit,
            supportModel:SupportModel?
        ) {
            binding.apply {
                tvNamePriest.text = supportModel?.name
                cvPriest.setOnClickListener {
                    listener(location, supportModel!!)
                }
            }
        }
    }
}