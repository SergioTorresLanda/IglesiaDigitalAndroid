package mx.arquidiocesis.eamxbiblioteca.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxbiblioteca.databinding.ItemSubcategoryBinding
import mx.arquidiocesis.eamxbiblioteca.models.Content

class SubcategoryAdapter(val contents: ArrayList<Content>, val listener: (Content) -> Unit) :
    RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubcategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSubcategoryBinding.inflate(inflater, parent, false)
        return SubcategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) =
        holder.bind(contents[position], listener)

    override fun getItemCount(): Int = contents.size

    class SubcategoryViewHolder(val binding: ItemSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: Content, listener: (Content) -> Unit) {
            binding.apply {
                tvSubcategoryName.text = content.name

                root.setOnClickListener {
                    listener(content)
                }
            }
        }
    }
}