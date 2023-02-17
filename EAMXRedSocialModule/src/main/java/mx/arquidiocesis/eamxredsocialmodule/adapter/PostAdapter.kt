package mx.arquidiocesis.eamxredsocialmodule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemFollowBinding
import mx.arquidiocesis.eamxredsocialmodule.model.PostModel


class PostAdapter(
    var list: MutableList<PostModel>,
    val listener: (PostModel) -> Unit
) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFollowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(listener, list[position])

    override fun getItemCount(): Int = list.size

    class ViewHolder(val binding: ItemFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {}
        }

        fun bind(
            listener: (PostModel) -> Unit,
            item: PostModel
        ) {
            binding.apply {
                /*txtName.text = item.name
                if (!item.image.isNullOrEmpty()) {
                    imgPriest.loadByUrlIntDrawableerror(item.image, R.drawable.user)
                }
                ivSegir.visibility = View.VISIBLE
                ivSeguiendo.visibility = View.GONE
                item.relationshipStatus?.let { r ->
                    if (r == 1 || r == 3) {
                        ivSegir.visibility = View.GONE
                        ivSeguiendo.visibility = View.VISIBLE
                    }
                }
                ivSegir.setOnClickListener {
                    listener(item)
                }*/
            }
        }
    }
}