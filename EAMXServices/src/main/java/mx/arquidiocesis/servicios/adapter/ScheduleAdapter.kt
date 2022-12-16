package mx.arquidiocesis.servicios.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.servicios.databinding.ItemScheduleHourBinding
import mx.arquidiocesis.servicios.model.Schedule
import kotlin.properties.Delegates

class ScheduleAdapter(val listener: (Int) -> Unit) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    var schedules: List<Schedule> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedPosition by Delegates.observable(-1) { property, oldPos, newPos ->
        if (newPos in schedules.indices) {
            notifyItemChanged(oldPos)
            notifyItemChanged(newPos)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemScheduleHourBinding.inflate(inflater, parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        if (position in schedules.indices) {
            holder.bind(schedules[position], position == selectedPosition)
            holder.binding.rbHour.setOnClickListener {
                selectedPosition = position
                listener(selectedPosition)
            }
        }
    }

    override fun getItemCount(): Int = schedules.size

    inner class ScheduleViewHolder(val binding: ItemScheduleHourBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(schedule: Schedule, selected: Boolean) {
            binding.tvHour.text = "${schedule.hour_start} hrs."
            binding.rbHour.isChecked = selected
            /*binding.rbHour.isChecked = when(itemCount==1){
                true -> true
                false -> selected
            }*/
        }
    }
}