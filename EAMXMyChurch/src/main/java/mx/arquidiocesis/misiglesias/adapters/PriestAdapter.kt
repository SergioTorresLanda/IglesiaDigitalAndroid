package mx.arquidiocesis.misiglesias.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.misiglesias.databinding.ItemPriestBinding
import mx.arquidiocesis.misiglesias.model.PriestModel

class PriestAdapter(
    val priest: MutableList<PriestModel>,
    var recyclerView: RecyclerView,
    val listener: (PriestModel) -> Unit
) :
    RecyclerView.Adapter<PriestAdapter.MassesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MassesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPriestBinding.inflate(inflater, parent, false)
        return MassesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MassesViewHolder, position: Int) =
        holder.bind(listener, priest[position], this)

    override fun getItemCount(): Int = priest.size
    fun updateReceiptsList(item: PriestModel) {
        priest.add(item)
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    fun getReceiptsList(): MutableList<PriestModel> {
        return priest
    }

    fun deleteReceiptsList(item: PriestModel) {
        priest.remove(item)
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    class MassesViewHolder(val binding: ItemPriestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
            }
        }

        fun bind(
            listener: (PriestModel) -> Unit, item: PriestModel,
            adapter: PriestAdapter
        ) {
            binding.tvActivity.text = item.name
            binding.ivRemoveActivity.setOnClickListener {
                adapter.deleteReceiptsList(item)
            }
            binding.root.setOnClickListener {
                listener(item)
            }
        }
    }
}