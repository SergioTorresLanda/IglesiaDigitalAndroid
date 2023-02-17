package mx.arquidiocesis.eamxgeneric.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.util.loadByUrlIntDrawableerror
import mx.arquidiocesis.eamxgeneric.databinding.ItemReleaseBinding
import mx.arquidiocesis.eamxgeneric.model.DataHomeReleaseResponse

class ViewPagerAdapter(
    private val releases: List<DataHomeReleaseResponse>,
    val listener: (String) -> Unit
) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding = ItemReleaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) =
        holder.bind(releases[position])

    override fun getItemCount() = releases.size

    class ViewPagerViewHolder(val binding: ItemReleaseBinding, val listener: (String) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(release: DataHomeReleaseResponse) {
            binding.apply {
                tvTitleRelease.text = release.title
                release.imageUrl?.let {
                    ivRelease.loadByUrlIntDrawableerror(
                        it,
                        R.drawable.ic_place_holder_by_pictures_upload
                    )
                }
                root.setOnClickListener { release.publishUrl?.let { it1 -> listener(it1) } }
            }
        }
    }
}