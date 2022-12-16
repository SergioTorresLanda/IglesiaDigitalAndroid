package mx.arquidiocesis.eamxprofilemodule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxItemActivityBinding
import mx.arquidiocesis.eamxprofilemodule.model.ActivitiesModel

class ActivitiesAdapter(
        var actividades: MutableList<ActivitiesModel>,
        var recyclerView: RecyclerView
) :
    RecyclerView.Adapter<ActivitiesAdapter.ViewHolder>() {

    private var enabledChild : Boolean = true
    lateinit var binding : EamxItemActivityBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = EamxItemActivityBinding.inflate(inflater, parent, false)
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

    class ViewHolder(val view: EamxItemActivityBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            activity: ActivitiesModel,
            enabled : Boolean,
            adapter: ActivitiesAdapter
        ) {
            view.tvActivity.text = activity.name

                view.llactivities.setOnClickListener {
                    adapter.deleteReceiptsList(activity)
                }
        }
    }
}