package mx.arquidiocesis.eamxprofilemodule.ui.promises.createpromise.fragment

import androidx.recyclerview.widget.DiffUtil
import mx.arquidiocesis.eamxprofilemodule.ui.promises.createpromise.modelo.Model

object EAMXDiffCallback : DiffUtil.ItemCallback<Model>() {

        override fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean {
            return oldItem.promisseDescription == newItem.promisseDescription
        }

        override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean {
            return oldItem == newItem
        }

}