package mx.arquidiocesis.registrosacerdote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.registrosacerdote.databinding.EamxItemPriestActivityBinding
import mx.arquidiocesis.registrosacerdote.model.ActivitiesModel

class ActivitiesAdapter(
    var actividades: MutableList<ActivitiesModel>,
    var recyclerView: RecyclerView
) :
    RecyclerView.Adapter<ActivitiesAdapter.ViewHolder>() {

    private var enabledChild : Boolean = true
    lateinit var binding : EamxItemPriestActivityBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = EamxItemPriestActivityBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(actividades[position], enabledChild, this)

    override fun getItemCount(): Int = actividades.size

    fun updateReceiptsList(activity: ActivitiesModel) {
        actividades.add(activity)
        recyclerView.post { notifyDataSetChanged() }
    }

    fun deleteReceiptsList(activity: ActivitiesModel) {
        actividades.remove(activity)
        recyclerView.post { notifyDataSetChanged() }
    }

    fun addItem(activity: ActivitiesModel, enabled : Boolean = true) {
        actividades.add(activity)
        enabledChild = enabled;
        recyclerView.post { notifyDataSetChanged() }
    }

    fun getList() = actividades

    fun existId(id: Int): Boolean  = actividades.any { it.id == id }

    fun clear() {
        actividades.clear()
        recyclerView.post { notifyDataSetChanged() }
    }

    class ViewHolder(val view: EamxItemPriestActivityBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            activity: ActivitiesModel,
            enabled : Boolean,
            adapter: ActivitiesAdapter
        ) {
            view.tvActivity.text = activity.name

            if(enabled) {
                view.llactivities.setOnClickListener {
                    adapter.deleteReceiptsList(activity)
                }
            }
        }
    }
}