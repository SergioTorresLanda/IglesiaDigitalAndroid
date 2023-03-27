package mx.arquidiocesis.eamxredsocialmodule.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_media_picker.view.*
import kotlinx.android.synthetic.main.item_red_social.*
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemMediaPickerBinding
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemRedSocialBinding
import mx.arquidiocesis.eamxredsocialmodule.model.MultimediaModel
import mx.arquidiocesis.eamxredsocialmodule.model.PostModel
import mx.arquidiocesis.eamxredsocialmodule.model.ResultMultiProfileModel
import mx.arquidiocesis.eamxredsocialmodule.ui.*
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class EAMXPublicationsAllAdapter(
    val context: Context,
    val listProfile: List<ResultMultiProfileModel>? = null,
    val isSuper: Boolean = false,
    val isPrincipal: Boolean = true,
) :
    RecyclerView.Adapter<EAMXPublicationsAllAdapter.NewsViewHolder>() {
    var items: ArrayList<PostModel> = ArrayList()

    lateinit var onItemClickListener: (PostModel, String) -> Unit
    private lateinit var contexto: Context

    /*expresión regular*/
    var URL_REGEX =
        "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" + "(%{2}|[-()_.!~*';/?:@&=+$, A-Za-z0-9])+)" + "([).!';/?:, ][[:blank:]])?$"
    var URL_PATTERN = Pattern.compile(URL_REGEX)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRedSocialBinding.inflate(inflater, parent, false)
        contexto = parent.context
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val cadenaModel = items[position]
        holder.bind(cadenaModel, listProfile)
    }

    inner class NewsViewHolder(private val binding: ItemRedSocialBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(
            item: PostModel, list: List<ResultMultiProfileModel>? = null,
        ) = with(binding) {
            val profileId = eamxcu_preferences.getData(
                EAMXEnumUser.USER_ID_REDSOCIAL.name,
                EAMXTypeObject.INT_OBJECT
            ) as Int
            var guest = eamxcu_preferences.getData(
                EAMXEnumUser.GUEST.name,
                EAMXTypeObject.BOOLEAN_OBJECT
            ) as Boolean
            var isMy = false
            if (item.scope.typeId!! > 1) {
                list?.let { p ->
                    p.forEach {
                        if (it.id == item.scope.id) {
                            isMy = true
                            return@forEach
                        }
                    }
                }
            } else {
                isMy = profileId == item.author.id
            }
            if (isSuper || isMy) {
                ivOption.visibility = View.VISIBLE
            } else {
                ivOption.visibility = View.GONE
            }
            txtName.text = item.author.name
            imgPriest.loadByUrl(item.author.image.toString(), false, true)
            val dateResponse = item.createdAt.toLong()
            val objectFormat = EAMXFormatDate(root.context)
            val miFecha = objectFormat.diferencia(dateResponse)
            txtDate.text = miFecha
            if (isPrincipal) {
                txtMessage.setOnClickListener {
                    onItemClickListener(item, "")
                }
            }
            txtMessage.buildTextSuccess(item.content, root.context)
            tvLike.text = item.totalReactions.toString()
            tvLikeDado.text = item.totalReactions.toString()
            tvComent.text = item.totalComments.toString()
            ivOption.setOnClickListener {
                val popupMenu: PopupMenu = PopupMenu(context, ivOption)
                popupMenu.menuInflater.inflate(
                    mx.arquidiocesis.eamxcommonutils.R.menu.menu_review,
                    popupMenu.menu
                )
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    when (it.itemId) {
                        mx.arquidiocesis.eamxcommonutils.R.id.editar ->
                            onItemClickListener(item, EDITAR)
                        mx.arquidiocesis.eamxcommonutils.R.id.eliminar ->
                            onItemClickListener(item, ELIMINAR)

                    }
                    true
                })
                popupMenu.show()
            }

            btnShare.setOnClickListener {
                //TEXTO
                if (item.content.isNotEmpty() && item.multimedia.filter { it.format == "image" }.size == 0 && item.multimedia.filter { it.format == "video/mp4" }.size == 0) {
                    val popupShare: PopupMenu = PopupMenu(context, btnShare)
                    popupShare.menuInflater.inflate(
                        mx.arquidiocesis.eamxcommonutils.R.menu.share_text,
                        popupShare.menu
                    )
                    popupShare.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        when (it.itemId) {
                            mx.arquidiocesis.eamxcommonutils.R.id.texto ->
                                onItemClickListener(item, TEXTO)
                        }
                        true
                    })
                    popupShare.show()
                }

                //IMAGEN
                if (item.content.isEmpty() && item.multimedia.filter { it.format == "image" }.size > 0 && item.multimedia.filter { it.format == "video/mp4" }.size == 0) {
                    val popupShare: PopupMenu = PopupMenu(context, btnShare)
                    popupShare.menuInflater.inflate(
                        mx.arquidiocesis.eamxcommonutils.R.menu.share_img,
                        popupShare.menu
                    )
                    popupShare.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        when (it.itemId) {
                            mx.arquidiocesis.eamxcommonutils.R.id.imagen ->
                                onItemClickListener(item, IMAGEN)
                        }
                        true
                    })
                    popupShare.show()
                }

                //VIDEO
                if (!item.content.isEmpty() && item.multimedia.filter { it.format == "image" }.size == 0 && item.multimedia.filter { it.format == "video/mp4" }.size > 0) {
                    val popupShare: PopupMenu = PopupMenu(context, btnShare)
                    popupShare.menuInflater.inflate(
                        mx.arquidiocesis.eamxcommonutils.R.menu.share_vid,
                        popupShare.menu
                    )
                    popupShare.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        when (it.itemId) {
                            mx.arquidiocesis.eamxcommonutils.R.id.video ->
                                onItemClickListener(item, VIDEO)
                        }
                        true
                    })
                    popupShare.show()
                }

                //TEXTO E IMAGEN
                if (item.content.isNotEmpty() && item.multimedia.filter { it.format == "image" }
                        .isNotEmpty() && item.multimedia.filter { it.format == "video/mp4" }.isEmpty()) {
                    val popupShareI: PopupMenu = PopupMenu(context, btnShare)
                    popupShareI.menuInflater.inflate(
                        mx.arquidiocesis.eamxcommonutils.R.menu.share_txt_img,
                        popupShareI.menu
                    )
                    popupShareI.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        when (it.itemId) {
                            mx.arquidiocesis.eamxcommonutils.R.id.texto ->
                                onItemClickListener(item, TEXTO)
                            mx.arquidiocesis.eamxcommonutils.R.id.imagen ->
                                onItemClickListener(item, IMAGEN)
                        }
                        true
                    })
                    popupShareI.show()
                }

                //TEXTO Y VIDEO
                if (item.content.isNotEmpty() && item.multimedia.filter { it.format == "image" }
                        .isEmpty() && item.multimedia.filter { it.format == "video/mp4" }
                        .isNotEmpty()) {
                    val popupShare: PopupMenu = PopupMenu(context, btnShare)
                    popupShare.menuInflater.inflate(
                        mx.arquidiocesis.eamxcommonutils.R.menu.share_txt_vid,
                        popupShare.menu
                    )
                    popupShare.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        when (it.itemId) {
                            mx.arquidiocesis.eamxcommonutils.R.id.texto ->
                                onItemClickListener(item, TEXTO)
                            mx.arquidiocesis.eamxcommonutils.R.id.video ->
                                onItemClickListener(item, VIDEO)
                        }
                        true
                    })
                    popupShare.show()
                }

                //IMAGEN Y VIDEO
                if (item.content.isEmpty() && item.multimedia.filter { it.format == "image" }
                        .isNotEmpty() && item.multimedia.filter { it.format == "video/mp4" }
                        .isNotEmpty()) {
                    val popupShare: PopupMenu = PopupMenu(context, btnShare)
                    popupShare.menuInflater.inflate(
                        mx.arquidiocesis.eamxcommonutils.R.menu.share_img_vid,
                        popupShare.menu
                    )
                    popupShare.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        when (it.itemId) {
                            mx.arquidiocesis.eamxcommonutils.R.id.imagen ->
                                onItemClickListener(item, IMAGEN)
                            mx.arquidiocesis.eamxcommonutils.R.id.video ->
                                onItemClickListener(item, VIDEO)
                        }
                        true
                    })
                    popupShare.show()
                }

                //TODOS
                if (item.content.isNotEmpty() && item.multimedia.filter { it.format == "image" }
                        .size > 0 && item.multimedia.filter { it.format == "video/mp4" }
                        .size > 0) {
                    val popupShare: PopupMenu = PopupMenu(context, btnShare)
                    popupShare.menuInflater.inflate(
                        mx.arquidiocesis.eamxcommonutils.R.menu.share_review,
                        popupShare.menu
                    )
                    popupShare.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        when (it.itemId) {
                            mx.arquidiocesis.eamxcommonutils.R.id.texto ->
                                onItemClickListener(item, TEXTO)
                            mx.arquidiocesis.eamxcommonutils.R.id.imagen ->
                                onItemClickListener(item, IMAGEN)
                            mx.arquidiocesis.eamxcommonutils.R.id.video ->
                                onItemClickListener(item, VIDEO)
                        }
                        true
                    })
                    popupShare.show()
                }
            }
            tvLike.visibility = View.VISIBLE
            tvLikeDado.visibility = View.GONE
            item.reaction?.let {
                tvLike.visibility = View.GONE
                tvLikeDado.visibility = View.VISIBLE
            }
            tvLike.setOnClickListener {
                onItemClickListener(item, LIKE)
            }
            tvLikeDado.setOnClickListener {
                onItemClickListener(item, LIKED)
            }
            ivAdd.setOnClickListener {
                onItemClickListener(item, SEGUIR)
            }
            tvComent.setOnClickListener {
                onItemClickListener(item, COMENTARIO)
            }
            /*
            tvShare.setOnClickListener {
                    onItemClickListener(item, COMPARTIR)
            }

             */
            iMediaGallery.cGallery.visibility(item.multimedia.isNotEmpty())
            if (isPrincipal) {
                iMediaGallery.cGallery.setOnClickListener {
                    onItemClickListener(item, "")
                }
            }
            resetContainer(iMediaGallery.iThumbnail1)
            resetContainer(iMediaGallery.iThumbnail2)
            resetContainer(iMediaGallery.iThumbnail3)
            resetContainer(iMediaGallery.iThumbnail4)
            for ((index, media) in item.multimedia.withIndex()) {
                when (index) {
                    0 -> {
                        displayContent(iMediaGallery.iThumbnail1, media)
                    }
                    1 -> {
                        displayContent(iMediaGallery.iThumbnail2, media)
                    }
                    2 -> {
                        displayContent(iMediaGallery.iThumbnail4, media)
                    }
                    3 -> {
                        displayContent(iMediaGallery.iThumbnail3, media)
                        if (item.multimedia.size > 4) {
                            iMediaGallery.iThumbnail4.vTransparency.visibility = View.VISIBLE
                            iMediaGallery.iThumbnail4.tvMoreMedia.visibility = View.VISIBLE
                            iMediaGallery.iThumbnail4.tvMoreMedia.text =
                                "+ ${item.multimedia.size - 3}"
                        }
                    }
                }
            }
            /*if (isPrincipal) {
                imgPriest.setOnClickListener {
                    onItemClickListener(item, PERFIL)
                }
            }*/
            if (!isPrincipal) {
                ivOption.visibility = View.GONE
                tvLike.visibility = View.GONE
                tvComent.visibility = View.GONE
                tvLikeDado.visibility = View.GONE
            }
            executePendingBindings()
            if (guest) {
                ivOption.visibility = View.GONE
            }
        }
    }

    private fun obt () {

    }
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }


    override fun getItemCount(): Int = items.size

    private fun displayContent(item: ItemMediaPickerBinding, media: MultimediaModel) {
        val placeHolderDrawable = ContextCompat.getDrawable(
            item.itemRoot.context,
            R.drawable.ic_placeholder
        )?.apply {
            setTintList(
                ContextCompat.getColorStateList(
                    item.itemRoot.context,
                    R.color.grayLevelThree
                )
            )
        }

        Glide.with(item.itemRoot.context)
            .load(media.url)
            .centerCrop()
            .thumbnail(0.1f)
            .placeholder(placeHolderDrawable)
            .into(item.ivThumbnail)

        item.itemRoot.visibility = View.VISIBLE
        if (!media.format.contains("image")) {
            item.vTransparency.visibility = View.VISIBLE
            item.ivPlay.visibility = View.VISIBLE
        }
    }

    private fun resetContainer(item: ItemMediaPickerBinding) {
        item.itemRoot.visibility = View.GONE
        item.vTransparency.visibility = View.GONE
        item.ivPlay.visibility = View.GONE
        item.vTransparency.visibility = View.GONE
        item.tvMoreMedia.visibility = View.GONE
    }

}
