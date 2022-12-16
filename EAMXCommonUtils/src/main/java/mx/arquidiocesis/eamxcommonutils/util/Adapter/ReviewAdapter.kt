package mx.arquidiocesis.eamxcommonutils.util.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import mx.arquidiocesis.eamxcommonutils.R
import mx.arquidiocesis.eamxcommonutils.databinding.ItemReviewBinding
import mx.arquidiocesis.eamxcommonutils.model.ReviewModel
import mx.arquidiocesis.eamxcommonutils.util.EAMXDiferenciaHoras

class ReviewAdapter(
    val context: Context,
    var schedules: MutableList<ReviewModel>,
    var recyclerView: RecyclerView,
    var isMyReview: Boolean,
    val listener: (ReviewModel,String) -> Unit
) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemReviewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return if(isMyReview&&position==0){
            holder.bind(context, listener, schedules[position], true, this)
        }else{
            holder.bind(context, listener, schedules[position], false, this)
        }
    }

    override fun getItemCount(): Int = schedules.size
    fun updateReceiptsList(item: ReviewModel) {
        schedules.add(item)
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    fun updateReceiptsList(item: MutableList<ReviewModel>) {
        schedules = item
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    fun deleteReceiptsList(item: ReviewModel) {
        schedules.remove(item)
        recyclerView.post(Runnable { notifyDataSetChanged() })
    }

    class ViewHolder(val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            listener: (ReviewModel,String) -> Unit,
            item: ReviewModel,
            isMyReview: Boolean,
            adapter: ReviewAdapter
        ) {
            binding.apply {
                if (isMyReview) {
                    ivAdminReview.visibility = View.VISIBLE
                }
                Glide.with(context)
                    .load(Uri.parse(item.devotee?.image_url))
                    .apply(RequestOptions().override(80, 80))
                    .into(ivDevotee)
                tvComentario.text = item.review
                tvNombreComentario.text = "${item.devotee?.name} ${item.devotee?.first_surname}"
                if(!item.creation_date.isNullOrEmpty()){
                    tvTime.text = EAMXDiferenciaHoras().main(item.creation_date)
                }
                if (!item.rating.isNullOrEmpty()) {
                    rbReview.rating = item.rating.toFloat()
                }
                ivAdminReview.setOnClickListener {
                    val popupMenu: PopupMenu = PopupMenu(context,ivAdminReview)
                    popupMenu.menuInflater.inflate(R.menu.menu_review,popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        when(it.itemId) {
                            R.id.editar ->
                                listener(item,"EDITAR")
                            R.id.eliminar ->
                                listener(item,"ELIMINAR")

                        }
                        true
                    })
                    popupMenu.show()

                }
            }


        }
    }
}