package mx.arquidiocesis.eamxgeneric.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxcommonutils.util.EAMXFirebaseManager
import mx.arquidiocesis.eamxcommonutils.util.extraIdUrlVideoYoutube
import mx.arquidiocesis.eamxcommonutils.util.loadByUrl
import mx.arquidiocesis.eamxgeneric.databinding.ItemSuggestionBinding
import mx.arquidiocesis.eamxgeneric.model.SuggestionModel

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

        fun bind(suggestionModel: SuggestionModel, listener: (SuggestionModel) -> Unit) {
            binding.apply {
                tvTitleSuggestion.text = suggestionModel.title
                val image = if (suggestionModel.type == VIDEO) {
                    "https://img.youtube.com/vi/${suggestionModel.article_url?.extraIdUrlVideoYoutube()}/0.jpg"
                } else {
                    suggestionModel.imageUrl
                }

                image?.let {
                    ivSuggestion.loadByUrl(it)
                }

                /*Glide.with(binding.root.context)
                    .load(image)
                    .into(ivSuggestion)*/

                root.setOnClickListener {
                    EAMXFirebaseManager(it.context).setLogEvent("screen_view_tag", Bundle().apply {
                        putString("screen_name", "Android_Sugerencias")
                    })
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