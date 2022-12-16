package mx.arquidiocesis.servicios.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.servicios.R
import mx.arquidiocesis.servicios.databinding.ItemServiciosBinding
import mx.arquidiocesis.servicios.model.ServiceMenuMainModel


class ServiceAdapter(
    val context: Context,
    var services: MutableList<ServiceMenuMainModel>,
    private val listener: (ServiceMenuMainModel) -> Unit
) :
    RecyclerView.Adapter<ServiceAdapter.ServicesViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServicesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServiciosBinding.inflate(inflater, parent, false)
        return ServicesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) =
        holder.bind(listener, services[position])

    override fun getItemCount(): Int = services.size

    class ServicesViewHolder(val binding: ItemServiciosBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listener: (ServiceMenuMainModel) -> Unit,
            service: ServiceMenuMainModel
        ) {
            binding.tvTitleService.text = service.title
            binding.tvDesc.text = service.desc

            when (service.img) {
                "Sacrament" -> {
                    binding.ivImgMenu.setBackgroundResource(R.drawable.ic_sacrament)
                }
                "Intentions" -> {
                    binding.ivImgMenu.setBackgroundResource(R.drawable.ic_intentions)
                }
                "OtherServices" -> {
                    binding.ivImgMenu.setBackgroundResource(R.drawable.ic_other_services)
                }
            }
            binding.root.setOnClickListener {
                listener(service)
            }
        }


    }
}