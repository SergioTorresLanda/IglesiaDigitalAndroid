package mx.arquidiocesis.misiglesias.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxcommonutils.util.convertToBitmap
import mx.arquidiocesis.misiglesias.databinding.ItemChurchFindedBinding
import mx.arquidiocesis.misiglesias.model.Location


class ChurchAdapter(
    val context: Context,
    val churches: List<Location>?,
    val listener: (Location) -> Unit
) :
    RecyclerView.Adapter<ChurchAdapter.ChurchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChurchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChurchFindedBinding.inflate(inflater, parent, false)
        return ChurchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChurchViewHolder, position: Int) =
        holder.bind(context, listener, churches?.get(position))

    override fun getItemCount(): Int = churches!!.size

    class ChurchViewHolder(val binding: ItemChurchFindedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            context: Context,
            listener: (Location) -> Unit,
            church: Location?
        ) {
            binding.tvTitulo.text = church?.name
            church?.arrayImage?.let { array ->
                val bitmap =  array.convertToBitmap()
                bitmap?.let {
                    binding.ivChurch.setImageBitmap(it)
                }
            } ?: kotlin.run {
                church?.image_url?.let {
                    Glide.with(context)
                        .load(Uri.parse(it))
                        .into(binding.ivChurch)
                }
            }

            if (!church!!.schedules.isNullOrEmpty()) {
                church.schedules.forEach {

                    if (!it.start_hour.isNullOrEmpty()) {
                        binding.tvHorarioss.text = "${it.start_hour}  "
                    }
                    if (!it.end_hour.isNullOrEmpty()) {
                        binding.tvHorarioss.text = "${binding.tvHorarioss.text} ${it.end_hour} \n"
                    }

                }
            }

            binding.root.setOnClickListener {
                if (church != null)
                    listener(church)
            }
        }
    }
}