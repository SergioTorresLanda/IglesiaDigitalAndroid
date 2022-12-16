package mx.arquidiocesis.eamxredsocialmodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        holder.bind(context, listener, list[position], listProfile, isSuper,this)

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
                item.scope?.let { s ->
                    s.typeId?.let { type ->
                        if (type > 1) {
                            var isMy = false
                            s.id.let { i ->
                                list?.let { p ->
                                    p.forEach {
                                        if (it.id == i) {
                                            isMy = true
                                            return@forEach
                                        }
                                    }
                                }
                            }
                            if (isMy||isSuper) {
                                ivOption.visibility = View.VISIBLE
                            } else {
                                ivOption.visibility = View.GONE
                            }
                            txtName.text = s.name
                            if (!s.image.isNullOrEmpty()) {
                                Glide.with(root.context)
                                    .load(s.image)
                                    .centerCrop()
                                    .into(imgPriest)
                            }
                        } else {
                            if (profileId == item.author.id||isSuper) {
                                ivOption.visibility = View.VISIBLE
                            } else {
                                ivOption.visibility = View.GONE
                            }
                            txtName.text = item.author.name
                            if (!item.author.image.isNullOrEmpty()) {
                                Glide.with(root.context)
                                    .load(item.author.image)
                                    .centerCrop()
                                    .into(imgPriest)
                            }
                        }
                    }
                }

                val dateResponse = item.createdAt.toLong()
                val objectFormat = EAMXFormatDate(root.context)
                val miFecha = objectFormat.diferencia(dateResponse)
                tvFechaReview.text = miFecha
                txtMessage.text = item.content
                tvLike.text = item.totalReactions.toString()
                ivOption.setOnClickListener {
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

                }
                tvResponder.setOnClickListener {
                    listener(COMENTARIO, item)
                }

            }
        }
    }
}