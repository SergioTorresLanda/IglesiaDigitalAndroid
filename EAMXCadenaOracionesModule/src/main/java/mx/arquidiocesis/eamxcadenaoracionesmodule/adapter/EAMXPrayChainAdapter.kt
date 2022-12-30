package mx.arquidiocesis.eamxcadenaoracionesmodule.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcadenaoracionesmodule.R
import mx.arquidiocesis.eamxcadenaoracionesmodule.databinding.ItemCadenaOracionBinding
import mx.arquidiocesis.eamxcadenaoracionesmodule.model.Data
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.dateFormatString
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxcommonutils.util.imagen.ImagenProfile
import mx.arquidiocesis.eamxcommonutils.util.toCalendar
import java.util.*

class EAMXPrayChainAdapter(
    private val listener: (Int, Boolean, ProgressBar) -> Unit
) : RecyclerView.Adapter<EAMXPrayChainAdapter.CadenaViewHolder>() {

    inner class CadenaViewHolder(val binding: ItemCadenaOracionBinding) :
        RecyclerView.ViewHolder(binding.root)

    val userLocal =
        eamxcu_preferences.getData(EAMXEnumUser.USER_ID.name, EAMXTypeObject.INT_OBJECT) as Int

    private val list: MutableList<Data> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CadenaViewHolder(
            ItemCadenaOracionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CadenaViewHolder, position: Int) {
        holder.binding.apply {
            list[position].let { item ->
                txtName.text = item.fiel_name
                txtDescripcion.text = item.description
                txtTimeDate.text =
                    item.creation_date.toCalendar("dd/MM/yyyy HH:mm:ss").getLabelDate()

                if (userLocal != item.id) {
                    if (item.imageProfile == "s/n" || item.imageProfile.isEmpty())
                        imgUser.setImageResource(R.drawable.user)
                    else com.bumptech.glide.Glide.with(imgUser)
                        .load(item.imageProfile)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                        .fitCenter()
                        .circleCrop()
                        .into(imgUser)
                } else {
                    ImagenProfile().loadImageProfile(imgUser, imgUser.context)
                }

                val likes = item.reaction.like.toIntOrNull() ?: 0
                txtCount.text = "$likes ${root.context.getString(if (likes == 1) R.string.playing_one else R.string.playing_more)}"
                if (item.praying != 0) ivPray.setImageResource(R.drawable.icono_manos_azul)
                val onClick = View.OnClickListener {
                    listener(item.id, item.praying == 0, pbOracion)
                }
                txtPlaying.setOnClickListener(onClick)
                imPray.setOnClickListener(onClick)
                ivPray.setOnClickListener(onClick)
            }
        }
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(listUpdate: List<Data>) {
        list.clear()
        list.addAll(listUpdate.toMutableList())
        notifyDataSetChanged()
    }


    private fun Calendar.getLabelDate(currentDate: Calendar = Calendar.getInstance()): String {
        fun Calendar.getDays() = get(Calendar.DAY_OF_YEAR) + ((get(Calendar.YEAR) - 1) * 365)
        fun Calendar.getMonths() = get(Calendar.MONTH) + 1 + ((get(Calendar.YEAR) - 1) * 12)
        fun Calendar.getWeeks() = get(Calendar.WEEK_OF_YEAR) - 1 + ((get(Calendar.YEAR) - 1) * 52)
        val diffDays = currentDate.getDays() - getDays()
        return if (diffDays <= 1) "${if (diffDays == 0) "Hoy" else "Ayer"} a la${
            if (this.get(Calendar.HOUR_OF_DAY) != 1) "s" else ""
        } ${dateFormatString("HH:mm", this)}"
        else if (diffDays < 7) "Hace $diffDays dias"
        else {
            val diffWeeks = currentDate.getWeeks() - getWeeks()
            if (diffWeeks < 4) "Hace $diffWeeks semana${if (diffWeeks > 1) "s" else ""}"
            else {
                val diffMonths = currentDate.getMonths() - this.getMonths()
                "Hace $diffMonths mes${if (diffMonths == 1) "" else "es"}"
            }
        }

    }
}
