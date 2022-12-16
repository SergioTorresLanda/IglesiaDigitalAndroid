package mx.arquidiocesis.misiglesias.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.misiglesias.R
import mx.arquidiocesis.misiglesias.databinding.ItemChurchFindedBinding
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_my_churches.*
import mx.arquidiocesis.misiglesias.model.*

class ChurchesFindedAdapter(
    var churches: List<IglesiaBusquedaModel>,
    val listener: (IglesiaBusquedaModel) -> Unit
) :
    RecyclerView.Adapter<ChurchesFindedAdapter.ChurchesFindedViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChurchesFindedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChurchFindedBinding.inflate(inflater, parent, false)
        return ChurchesFindedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChurchesFindedViewHolder, position: Int) =
        holder.bind(listener, churches[position])

    override fun getItemCount(): Int = churches.size

    class ChurchesFindedViewHolder(val binding: ItemChurchFindedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: (IglesiaBusquedaModel) -> Unit, church: IglesiaBusquedaModel) {
            binding.tvTitulo.text = church.name

            if (church.image_url != null){
                Glide.with(binding.root.context)
                    .load(Uri.parse(church.image_url))
                    .into(binding.ivChurch)
            }else{
                binding.ivChurch.setImageDrawable(binding.root.context.getDrawable(R.drawable.emptychurch))
            }

            binding.root.setOnClickListener {
                listener(church)
            }
        }
    }
}