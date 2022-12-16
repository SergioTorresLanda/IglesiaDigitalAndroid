package mx.arquidiocesis.eamxprofilemodule.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxprofilemodule.R
import mx.arquidiocesis.eamxprofilemodule.databinding.ItemDiaconoProfileBinding
import mx.arquidiocesis.eamxprofilemodule.model.ChurchModel

class DiaconoChurchAdapter(
    var churchList: MutableList<ChurchModel>,
    var recyclerView: RecyclerView,
) :
    RecyclerView.Adapter<DiaconoChurchAdapter.ViewHolder>() {

    private var enabledChild: Boolean = true
    lateinit var binding: ItemDiaconoProfileBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemDiaconoProfileBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(churchList[position], this, enabledChild)
    }

    override fun getItemCount(): Int = churchList.size

    fun deleteReceiptsList(church: ChurchModel) {
        churchList.remove(church)
        recyclerView.post { notifyDataSetChanged() }
    }

    fun addItem(church: ChurchModel, enabled: Boolean = true) {
        churchList.add(church)
        enabledChild = enabled
        recyclerView.post { notifyDataSetChanged() }
    }

    fun clear() {
        churchList.clear()
        recyclerView.post { notifyDataSetChanged() }
    }

    fun getList() = churchList

    fun existId(id: Int): Boolean = churchList.any { it.id == id }

    class ViewHolder(val binding: ItemDiaconoProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            church: ChurchModel,
            adapter: DiaconoChurchAdapter,
            enabled: Boolean,
        ) {
            binding.apply {
                tvTitleChurchDiacon.text = church.name
                church.imageUrl?.let {
                    Glide.with(root.context)
                        .load(Uri.parse(church.imageUrl))
                        .error(root.context.getDrawable(R.drawable.emptychurch))
                        .into(ivChurchDiacon)
                }?: run {
                    Glide.with(root.context)
                        .load(root.context.getDrawable(R.drawable.emptychurch))
                        .into(ivChurchDiacon)
                }

                ivCleanChurchDiaco.setOnClickListener { adapter.deleteReceiptsList(church) }
                ivCleanChurchDiaco.isEnabled = enabled
            }
        }
    }
}