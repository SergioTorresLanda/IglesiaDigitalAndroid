package mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemRactionsAllBinding
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.EamxBottomSheetDialogFragment.Companion.TAG
import mx.arquidiocesis.eamxredsocialmodule.news.detail.bottomship.model.getpublitaion.Autor

class EAMXAutorAdapter : ListAdapter<Autor, EAMXAutorAdapter.AutorViewHolder>(EAMXDiffCallback) {

    lateinit var onItemClickListener: (Autor) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutorViewHolder {
        val binding = ItemRactionsAllBinding.inflate(LayoutInflater.from(parent.context))
        return AutorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AutorViewHolder, position: Int) {
        val autorModel = getItem(position)
        holder.bind(autorModel)
    }

    inner class AutorViewHolder(private val binding: ItemRactionsAllBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(autorModel: Autor) = with(binding) {
            txtName.text = autorModel.nombre
            Glide.with(binding.root.context)
                .load(autorModel.imagen)
                .centerCrop()
                .into(imgPriest)

            root.setOnClickListener {
                if (::onItemClickListener.isInitialized) {
                    onItemClickListener(autorModel)
                } else {
                    Log.e(TAG, root.context.getString(R.string.on_item_click_listener_no_isinitialized))
                }
            }
            executePendingBindings()
        }
    }
}
