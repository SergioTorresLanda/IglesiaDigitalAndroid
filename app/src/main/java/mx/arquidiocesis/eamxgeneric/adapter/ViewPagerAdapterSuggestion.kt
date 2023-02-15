package mx.arquidiocesis.eamxgeneric.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxcommonutils.util.extraIdUrlVideoYoutube
import mx.arquidiocesis.eamxgeneric.databinding.ItemSuggestionBinding
import mx.arquidiocesis.eamxgeneric.model.SuggestionModel
import mx.arquidiocesis.eamxcommonutils.util.isUrlYoutube
import mx.arquidiocesis.eamxcommonutils.util.loadByUrlIntDrawableerror
import mx.arquidiocesis.eamxredsocialmodule.R

class ViewPagerAdapterSuggestion(
    private val suggestions: List<SuggestionModel>,
    private val listener: (SuggestionModel) -> Unit
) :
    RecyclerView.Adapter<ViewPagerAdapterSuggestion.SuggestionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val binding =
            ItemSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) =
        holder.bind(suggestions[position], listener)

    override fun getItemCount() = suggestions.size

    class SuggestionViewHolder(val binding: ItemSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val VIDEO = "VIDEO"
        val AUDIO = "AUDIO"
        val PDF = "PDF"
        val LINK = "LINK"

        fun bind(suggestionModel: SuggestionModel, listener: (SuggestionModel) -> Unit) {
            binding.apply {
                tvTitleSuggestion.text = suggestionModel.title
                var draw:Int = R.drawable.ic_place_holder_by_pictures_upload
                var img: String? = suggestionModel.imageUrl
                ivSuggestion.apply {
                    if (suggestionModel.type == AUDIO) {
                        draw = R.drawable.exo_icon_circular_play
                    } else if (suggestionModel.type == PDF) {
                        draw = R.drawable.ic_pdfviewpager
                    } else if (suggestionModel.type == VIDEO) {
                        if (suggestionModel.article_url!!.isUrlYoutube()) {
                            draw = com.upax.formacion.R.drawable.ic_player
                            img = "https://img.youtube.com/vi/${suggestionModel.article_url.extraIdUrlVideoYoutube()}/0.jpg"
                        } else {
                            draw = R.drawable.ic_video
                        }
                    } else if (suggestionModel.type == LINK) {
                        if (!suggestionModel.article_url.isNullOrEmpty()) {
                            draw = com.upax.formacion.R.drawable.ic_empty_library
                        }
                    }
                    img?.let {
                        loadByUrlIntDrawableerror(
                            it,
                            draw
                        )
                    }
                }
                root.setOnClickListener {
                    /*EAMXFirebaseManager(it.context).setLogEvent("screen_view", Bundle().apply {
                        putString("screen_class", "Sugerencias")
                    })*/
                    listener(suggestionModel)
                }
            }

            /*binding.root.setOnClickListener {
                val urlString = suggestionModel.
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage("com.android.chrome")
                try {
                    binding.root.context.startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    intent.setPackage(null)
                    binding.root.context.startActivity(intent)
                }
            }*/
        }
    }
}