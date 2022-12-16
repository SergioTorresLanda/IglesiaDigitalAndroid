package mx.arquidiocesis.eamxredsocialmodule.adapter

import androidx.recyclerview.widget.DiffUtil
import mx.arquidiocesis.eamxredsocialmodule.model.Publication

object EAMXDiffCallback : DiffUtil.ItemCallback<Publication>() {

        override fun areItemsTheSame(oldItem: Publication, newItem: Publication): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Publication, newItem: Publication): Boolean {
            return oldItem == newItem
        }

}