package mx.arquidiocesis.eamxredsocialmodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumUser
import mx.arquidiocesis.eamxcommonutils.common.EAMXTypeObject
import mx.arquidiocesis.eamxcommonutils.util.EAMXFormatDate
import mx.arquidiocesis.eamxcommonutils.util.eamxcu_preferences
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemCommentBinding
import mx.arquidiocesis.eamxredsocialmodule.model.CommentModel
import mx.arquidiocesis.eamxredsocialmodule.model.ResultMultiProfileModel
import mx.arquidiocesis.eamxredsocialmodule.ui.COMENTARIO
import mx.arquidiocesis.eamxredsocialmodule.ui.EDITAR
import mx.arquidiocesis.eamxredsocialmodule.ui.ELIMINAR
import kotlin.random.Random

class CommentAdapter(
    val context: Context,
    var list: MutableList<CommentModel>,
    var recyclerView: RecyclerView,
    val listProfile: List<ResultMultiProfileModel>? = null,
    val isSuper: Boolean = false,
    val listener: (String, CommentModel) -> Unit
) :
    RecyclerView.Adapter<CommentAdapter.MassesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MassesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(inflater, parent, false)
        return MassesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MassesViewHolder, position: Int) =
        holder.bind(context, listener, list[position], listProfile, isSuper, this)

    override fun getItemCount(): Int = list.size

    fun updateReceiptsList(item: CommentModel) {
        list.add(item)
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    fun updateReceiptsList(item: MutableList<CommentModel>) {
        list = item

        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    fun deleteReceiptsList(item: CommentModel) {
        list.remove(item)

        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    class MassesViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
            }
        }

        fun bind(
            context: Context,
            listener: (String, CommentModel) -> Unit,
            item: CommentModel,
            list: List<ResultMultiProfileModel>? = null,
            isSuper: Boolean,
            adapter: CommentAdapter
        ) {
            binding.apply {
                val profileId = eamxcu_preferences.getData(
                    EAMXEnumUser.USER_ID_REDSOCIAL.name,
                    EAMXTypeObject.INT_OBJECT
                ) as Int
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
                if (!item.author.image.isNullOrEmpty()) {
                    Glide.with(root.context)
                        .load(item.author.image+"?"+Random.nextInt(0,200))
                        .centerCrop()
                        .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imgPriest)
                }
                val dateResponse = item.createdAt.toLong()
                val objectFormat = EAMXFormatDate(root.context)
                val miFecha = objectFormat.diferencia(dateResponse)
                tvFechaReview.text = miFecha
                txtMessage.text = item.content
                //tvLike.text = item.totalReactions.toString()
                tvLike.visibility = View.GONE
                /*ivOption.setOnClickListener {
                    Log.d("ClicComment","True")
                    val popupMenu: PopupMenu = PopupMenu(context, ivOption)
                    popupMenu.menuInflater.inflate(
                        mx.arquidiocesis.eamxcommonutils.R.menu.menu_review,
                        popupMenu.menu
                    )
                    popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        when (it.itemId) {
                            mx.arquidiocesis.eamxcommonutils.R.id.editar ->
                                listener(EDITAR, item)
                            mx.arquidiocesis.eamxcommonutils.R.id.eliminar ->
                                listener(ELIMINAR, item)

                        }
                        true
                    })
                    popupMenu.show()

                }*/
                /*tvResponder.setOnClickListener {
                    listener(COMENTARIO, item)
                }*/
                tvResponder.visibility = View.GONE
            }
        }
    }
}