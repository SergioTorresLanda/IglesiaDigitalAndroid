package mx.upax.formacion.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upax.formacion.R
import com.upax.formacion.databinding.ItemFormationNewBinding
import mx.arquidiocesis.eamxcommonutils.util.extraIdUrlVideoYoutube
import mx.upax.formacion.model.BaseModel
import mx.upax.formacion.ui.PDF
import mx.upax.formacion.ui.WEB
import mx.upax.formacion.ui.YOUTUBE
import java.text.DecimalFormat
import java.text.NumberFormat

class OthersNewAdapter(
    private val originalList: List<BaseModel>,
    private val listener: (BaseModel) -> Unit
) : RecyclerView.Adapter<OthersNewAdapter.ChurchViewHolder>() {

    private var category: String? = null
    private var searchText: String? = null
    private val listFilter = mutableListOf(*originalList.toTypedArray())

    inner class ChurchViewHolder(val binding: ItemFormationNewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ChurchViewHolder(
            ItemFormationNewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ChurchViewHolder, position: Int) {
        holder.binding.apply {
            listFilter[position].let { item ->
                this.item = item
                when (item.type) {
                    PDF, WEB -> {
                        if (item.image.trim().isEmpty()) {
                            ivResource.setImageResource(R.drawable.ic_empty_library)
                        } else
                            Glide.with(ivResource)
                                .load(Uri.parse(item.image)).centerCrop().fitCenter()
                                .into(ivResource)
                    }

                    YOUTUBE -> {
                        val idYoutube = item.url.extraIdUrlVideoYoutube()
                        Glide.with(ivResource)
                            .load("https://img.youtube.com/vi/$idYoutube/0.jpg")
                            .centerCrop()
                            .error(R.drawable.ic_player)
                            .into(ivResource)
                    }
                }

                root.setOnClickListener {
                    val format: NumberFormat = DecimalFormat("#,###")
                    tvView.text = root.context.getString(
                        R.string.card_label_views,
                        format.format(item.views + 1)
                    )
                    listener(item)
                }
            }
        }

    }

    fun setTextFilter(txt: String?): Boolean {
        searchText = txt
        return filterList()
    }


    fun setCategory(category: String?): Boolean {
        this.category = category
        return filterList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterList(): Boolean {
        listFilter.clear()
        listFilter.addAll(
            originalList.filter { category == null || category == it.type }
                .filter {
                    searchText.isNullOrEmpty() || it.title.uppercase()
                        .contains(searchText?.uppercase() ?: "")
                })
        notifyDataSetChanged()
        return listFilter.isNotEmpty()
    }


    override fun getItemCount() = listFilter.size


}