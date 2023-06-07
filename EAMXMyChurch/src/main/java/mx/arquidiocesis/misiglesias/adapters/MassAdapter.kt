package mx.arquidiocesis.misiglesias.adapters
//Este es el adapter de la primera tabla de horarios de misa (no edit)
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.misiglesias.databinding.ItemServiceScheduleBinding
import mx.arquidiocesis.misiglesias.model.ScheduleMass

class MassAdapter(
    var messes: MutableList<ScheduleMass>,
    var isEdit: Boolean,
    var rvHorarios: RecyclerView,
) :
    RecyclerView.Adapter<MassAdapter.MassesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MassesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServiceScheduleBinding.inflate(inflater, parent, false)
        return MassesViewHolder(binding, isEdit)
    }

    override fun onBindViewHolder(holder: MassesViewHolder, position: Int) =
        holder.bind(messes[position], this)

    override fun getItemCount(): Int = messes.size

    fun deleteReceiptsList(item: ScheduleMass) {
        messes.remove(item)
        rvHorarios.post { notifyDataSetChanged() }
    }

    class MassesViewHolder(val binding: ItemServiceScheduleBinding, val isEdit: Boolean) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(scheduleMass: ScheduleMass, massAdapter: MassAdapter) {
            binding.apply {
                tvServicio.text = scheduleMass.day
                scheduleMass.hours.forEach {
                    tvDia.text = "${tvDia.text} ${it.start_hour} "
                }
            }

            if (isEdit) {
                binding.ivRemove.visibility = View.VISIBLE
            }

            binding.ivRemove.setOnClickListener {
                massAdapter.deleteReceiptsList(scheduleMass)
            }
        }
    }
}