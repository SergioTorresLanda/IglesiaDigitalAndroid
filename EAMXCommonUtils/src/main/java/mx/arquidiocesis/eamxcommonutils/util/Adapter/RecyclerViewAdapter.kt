package mx.arquidiocesis.eamxcommonutils.util.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcommonutils.databinding.ItemAdapterBinding
import mx.arquidiocesis.eamxcommonutils.util.log

class RecyclerViewAdapter(
    var list: List<String>,
    val listener: (String) -> Unit
) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAdapterBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(list[position], listener)

    override fun getItemCount(): Int = list.size

    class ViewHolder(val binding: ItemAdapterBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, listener: (String) -> Unit) {
            binding.tvContent.text = item
            binding.cvComunity.setOnClickListener {
                listener(item)
                "FLOW EAMXRegisterCommunityFragment RecyclerViewAdapter".log()
            }
        }
    }
}