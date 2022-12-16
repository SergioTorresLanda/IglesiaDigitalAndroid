package mx.arquidiocesis.eamxbiblioteca.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxbiblioteca.databinding.ItemCategoryBinding
import mx.arquidiocesis.eamxbiblioteca.models.Category

class CategoryAdapter(val categories: List<Category>, val listener: (Category) -> Unit) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryBinding.inflate(inflater, parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) = holder.bind(categories[position], listener)

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, listener: (Category) -> Unit) {
            binding.apply {
                Glide.with(binding.root.context)
                    .load(Uri.parse(category.image))
                    .into(ivCategory)

                tvNameCategory.text = category.name
            }

            binding.root.setOnClickListener {
                listener(category)
            }
        }
    }
}