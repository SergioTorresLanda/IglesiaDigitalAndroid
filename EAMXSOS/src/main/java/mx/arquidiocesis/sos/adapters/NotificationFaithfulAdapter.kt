package  mx.arquidiocesis.sos.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.ItemNotificationChurchBinding
import mx.arquidiocesis.sos.model.ServicesPriestAdapter
import mx.arquidiocesis.sos.model.ServicesPriestModel

class NotificationFaithfulAdapter( servicesPriest : List<ServicesPriestModel>,
                                  val context : Context,
                                  val listener: (ServicesPriestModel) -> Unit,
) :
    RecyclerView.Adapter<NotificationFaithfulAdapter.NotificationChurchHolder>() {

    var localServicesPriest : List<ServicesPriestAdapter>? = null

    init{
        localServicesPriest = servicesPriest.groupBy { it.location.id }.map {
            ServicesPriestAdapter(
                location =  it.value[0].location,
                servicesPriestModel = it.value
            )
        }.toList()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationFaithfulAdapter.NotificationChurchHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotificationChurchBinding.inflate(inflater, parent, false)
        return NotificationChurchHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NotificationFaithfulAdapter.NotificationChurchHolder,
        position: Int
    ) {
        holder.bind(listener, localServicesPriest?.get(position))
    }

    override fun getItemCount(): Int = localServicesPriest?.size ?: 0

    inner class NotificationChurchHolder(val binding: ItemNotificationChurchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listener: (ServicesPriestModel) -> Unit,
            servicesPriest: ServicesPriestAdapter?
        ) {
            binding.apply {
                tvNameChruch.text = servicesPriest?.location?.name

                val kilometer  = String.format( "%.2f", (servicesPriest?.location?.distance!! / 1000))
                tvDistance.text = context.getString(R.string.text_km_card, kilometer)

                Glide.with(context)
                    .load(Uri.parse(servicesPriest?.location.image_url))
                    .error(R.drawable.ic_church)
                    .transform(CenterCrop(), RoundedCorners(24))
                    .into(ivChurchSOS)

                rvNotificationSOS.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
                    adapter = ChildNotificationFaithfulAdapter(servicesPriest.servicesPriestModel,listener)
                }
            }
        }
    }
}