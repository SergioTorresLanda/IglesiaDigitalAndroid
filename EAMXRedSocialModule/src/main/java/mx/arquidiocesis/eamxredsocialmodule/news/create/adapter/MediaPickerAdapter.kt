package mx.arquidiocesis.eamxredsocialmodule.news.create.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import mx.arquidiocesis.eamxredsocialmodule.R
import mx.arquidiocesis.eamxredsocialmodule.databinding.ItemMediaPickerBinding
import mx.arquidiocesis.eamxredsocialmodule.viewmodel.RedSocialViewModel


class MediaPickerAdapter(val items: ArrayList<ClipData.Item> = ArrayList(), var height: Int,val viewModel:RedSocialViewModel,val recyclerView: RecyclerView?=null) :
    RecyclerView.Adapter<MediaPickerAdapter.ViewHolder>() {

    var onItemClickListener: (ClipData.Item) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_media_picker, parent, false)
        val vh = ViewHolder(itemView)
        return vh
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val lp = holder.itemView.layoutParams
                var sglp = lp as StaggeredGridLayoutManager.LayoutParams
//                sglp.isFullSpan = items.count() == 1 || (position == 2 && items.count() == 3)
                if (items.count() == 1)
                {
                    sglp = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) as StaggeredGridLayoutManager.LayoutParams
                    sglp.height = height
                }else if (items.count() == 1 || (position == 0 && items.count() == 3)) {
                    sglp.height = height
                }
                holder.itemView.layoutParams = sglp
                val lm = (holder.itemView.parent as RecyclerView).layoutManager as StaggeredGridLayoutManager?
                lm!!.invalidateSpanAssignments()
                holder.itemView.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })

        if (position > 3) {
            holder.vBind.itemRoot.layoutParams = ViewGroup.LayoutParams(0, 0)
            return
        }

        val item = items[position]
        if (items.size > 2) {
            holder.vBind.itemRoot.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,this@MediaPickerAdapter.height / 2)
        }

        if (position == 3 && items.size > 4) {
            holder.vBind.vTransparency.visibility = View.VISIBLE
            holder.vBind.tvMoreMedia.visibility = View.VISIBLE
            holder.vBind.tvMoreMedia.text = "+ ${items.size - 4}"
        } else {
            holder.vBind.vTransparency.visibility = View.GONE
            holder.vBind.tvMoreMedia.visibility = View.GONE
        }

        Glide
            .with(holder.itemView.context)
            .asBitmap()
            .load(item.uri)
            .into(holder.vBind.ivThumbnail)

        val meta = viewModel.getFileMetaData(item.uri)

        if (meta?.isImage() == false) {
            holder.vBind.ivPlay.visibility = View.VISIBLE
            holder.vBind.vTransparency.visibility = View.VISIBLE
        }

        holder.vBind.itemRoot.setOnClickListener {
            onItemClickListener(item)
        }
    }
    fun updateReceiptsList() {
        recyclerView!!.post(Runnable { notifyDataSetChanged() })
    }
    override fun getItemCount(): Int = items.size

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor =
            context.contentResolver.query(contentUri, proj, null, null, null) ?: return null
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(columnIndex)
        }
        cursor.close()
        return res
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vBind = ItemMediaPickerBinding.bind(itemView).apply {
            itemRoot.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            itemRoot.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }
}