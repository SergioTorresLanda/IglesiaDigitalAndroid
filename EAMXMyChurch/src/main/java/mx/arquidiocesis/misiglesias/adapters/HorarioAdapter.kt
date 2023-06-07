package mx.arquidiocesis.misiglesias.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.misiglesias.databinding.ItemServiceScheduleBinding
import mx.arquidiocesis.misiglesias.model.HoraryModelItem
import mx.arquidiocesis.misiglesias.utils.PublicFunctions

//Horarios de misa edicion.
class HorarioAdapter(
    val edit:Boolean=false,
    var schedules: MutableList<HoraryModelItem>,
    var recyclerView: RecyclerView,
    val listener: (HoraryModelItem) -> Unit
) :
    RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorarioViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServiceScheduleBinding.inflate(inflater, parent, false)

        return HorarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) =
        holder.bind( edit,listener, schedules[position], this)

    override fun getItemCount(): Int = schedules.size
    fun updateReceiptsList(item: HoraryModelItem) {
        schedules.add(item)
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    fun updateReceiptsList(item: MutableList<HoraryModelItem>) {
        schedules = item
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    fun deleteReceiptsList(item: HoraryModelItem) {
        schedules.remove(item)
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    class HorarioViewHolder(val binding: ItemServiceScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            edit: Boolean,
            listener: (HoraryModelItem) -> Unit,
            item: HoraryModelItem,
            adapter: HorarioAdapter
        ) {
            //binding.tvDia.visibility = View.GONE
            binding.tvHorario.visibility = View.VISIBLE
            binding.tvHorario.text = " "
            binding.tvServicio.text = PublicFunctions().obtenerDias(item.days)
            binding.tvServicio.textSize = 16F
            if(!item.hour_start.isNullOrEmpty()){
                binding.tvDia.text = "${item.hour_start}"
            }
           /* if(!item.hour_end.isNullOrEmpty()){
                binding.tvHorario.text = "${binding.tvHorario.text} ${item.hour_end} "
            }*/
            if (edit){
                binding.ivRemove.visibility=View.VISIBLE
            }
            binding.ivRemove.setOnClickListener {
               adapter.deleteReceiptsList(item)
            }
        }
    }
}