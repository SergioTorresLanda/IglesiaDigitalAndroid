package mx.arquidiocesis.eamxbiblioteca.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.with
import com.bumptech.glide.request.RequestOptions
import mx.arquidiocesis.eamxbiblioteca.R
import mx.arquidiocesis.eamxbiblioteca.databinding.ItemDocumentoBinding
import mx.arquidiocesis.eamxbiblioteca.databinding.ItemVideoBinding
import mx.arquidiocesis.eamxbiblioteca.databinding.ItemVideoListBinding
import mx.arquidiocesis.eamxbiblioteca.models.ResourcesModel
import mx.arquidiocesis.eamxcommonutils.util.extraIdUrlVideoYoutube


class AdapterResources(
    var resources: List<ResourcesModel>,
    var context: Context,
    val type: Int,
    val listener: (ResourcesModel) -> Unit
)  :
    RecyclerView.Adapter<AdapterResources.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                when(type) {
                    1 -> R.layout.item_documento
                    2 -> R.layout.item_video
                    3 -> R.layout.item_video_list
                    else ->  R.layout.item_documento
                },
                parent,false
            ), type
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(resources[position], listener, context)

    override fun getItemCount(): Int = resources.size

    class ViewHolder(val view: View, private val type: Int) : RecyclerView.ViewHolder(view) {

        fun bind(resources: ResourcesModel, listener: (ResourcesModel) -> Unit, context: Context) {
            when (type) {
                1 -> {
                    val binding = ItemDocumentoBinding.bind(view)
                    binding.tvLinkDocuemento.text = resources.url
                    binding.tvdocumento.text = resources.description
                    binding.clDocumento.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.url))
                        context.startActivity(intent)
                    }
                }
                2 -> {
                    val binding = ItemVideoBinding.bind(view)
                    binding.tvVideo.text = resources.title
                    with(context).load(Uri.parse("https://img.youtube.com/vi/" + resources.url.extraIdUrlVideoYoutube() + "/0.jpg"))
                        .apply(RequestOptions().override(300, 300))
                        .into(binding.ivVideo)
                    binding.cvVideo.setOnClickListener {
                       listener(resources)
                    }
                }
                3 -> {
                    val binding = ItemVideoListBinding.bind(view)
                    binding.tvTitulo.text = resources.title
                    binding.tvContenido.text = resources.description

                }
                else -> { // Note the block

                }
            }
        }
    }
}