package mx.arquidiocesis.eamxgeneric.adapter

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.loadByUrl
import mx.arquidiocesis.eamxgeneric.databinding.ItemReleaseBinding
import mx.arquidiocesis.eamxgeneric.model.DataHomeReleaseResponse
import mx.arquidiocesis.eamxgeneric.model.SuggestionModel

class ViewPagerAdapter(private val releases: List<DataHomeReleaseResponse>, val listener: (String) -> Unit) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding = ItemReleaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerViewHolder(binding,listener)
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
                    ivRelease.loadByUrl(it)
                }
            }

            binding.root.setOnClickListener {
                EAMXFirebaseManager(it.context).setLogEvent("screen_view_tag", Bundle().apply {
                    putString("screen_name", "Android_DesdeLaFe")
                })
                val urlString = release.publishUrl
                if(!urlString.isNullOrEmpty()){
                    listener(urlString)
                }
               /* val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage("com.android.chrome")
                try {
                    binding.root.context.startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    intent.setPackage(null)
                    binding.root.context.startActivity(intent)
                }*/
            }
        }
    }
}