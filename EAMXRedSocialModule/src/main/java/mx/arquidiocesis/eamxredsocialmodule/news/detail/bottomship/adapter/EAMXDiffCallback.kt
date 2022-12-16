package mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.adapter

import androidx.recyclerview.widget.DiffUtil
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.getpublitaion.Autor

object EAMXDiffCallback : DiffUtil.ItemCallback<Autor>() {

        override fun areItemsTheSame(oldItem: Autor, newItem: Autor): Boolean {
            return oldItem.FIIDEMPLEADO == newItem.FIIDEMPLEADO
        }

        override fun areContentsTheSame(oldItem: Autor, newItem: Autor): Boolean {
            return oldItem == newItem
        }
}