package mx.arquidiocesis.eamxprofilemodule.ui.promises.createpromise.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxProfilePromiseItemSaintBinding
import mx.arquidiocesis.eamxprofilemodule.ui.fragments.createpromise.entities.EAMXSaintModel

class  EAMXProfielPromisesAdapter :
    ListAdapter<EAMXSaintModel, EAMXProfielPromisesAdapter.SaintHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<EAMXSaintModel>() {

        override fun areItemsTheSame(oldItem: EAMXSaintModel, newItem: EAMXSaintModel): Boolean {
            return oldItem.name == newItem.name //Igualamos si el item nuevo con el viejo es el mismo
        }

        override fun areContentsTheSame(oldItem: EAMXSaintModel, newItem: EAMXSaintModel): Boolean {
            return oldItem == newItem //para igualar modelos debe de ser una data class
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaintHolder =
        SaintHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.eamx_profile_promise_item_saint, parent, false)
        )


    override fun onBindViewHolder(holder: SaintHolder, position: Int) {
        val saintModel = getItem(position)
        holder.vBind.imgSaint.setImageDrawable(saintModel.drawable)
    }


    inner class SaintHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vBind = EamxProfilePromiseItemSaintBinding.bind(itemView)
    }
}

