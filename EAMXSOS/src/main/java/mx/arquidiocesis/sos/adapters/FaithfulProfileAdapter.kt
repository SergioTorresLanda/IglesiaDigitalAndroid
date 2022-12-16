package mx.arquidiocesis.sos.adapters

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upax.eamxsos.R
import com.upax.eamxsos.databinding.ItemServicesBinding
import kotlinx.android.synthetic.main.item_services.view.*
import mx.arquidiocesis.sos.model.Service

class FaithfulProfileAdapter(
    var list: List<Service>,
    val listener: (Pair<Int, Service>) -> Unit
) : RecyclerView.Adapter<FaithfulProfileAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val binding = ItemServicesBinding.inflate(inflate, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, position)
    }

    fun update(position: Int) {
        list[position].select = true;
        list.forEach {
            if (itemCount != position) {
                it.select = false;
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(val binding: ItemServicesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(service: Service, position: Int) {

            binding.apply {
                tvNameService.text = service.name

                if(service.description.trim().isEmpty()){
                    tvDescription.visibility = GONE
                }else{
                    tvDescription.text = service.description
                }

                cvItemService.setOnClickListener {
                    listener(Pair(position, service))
                    service.select = !service.select
                }

                if (service.select) {
                    ivSelect.setImageResource(R.drawable.ic_selecion)
                } else {
                    ivSelect.setImageResource(R.drawable.ic_non_select)
                }
            }
        }
    }
}