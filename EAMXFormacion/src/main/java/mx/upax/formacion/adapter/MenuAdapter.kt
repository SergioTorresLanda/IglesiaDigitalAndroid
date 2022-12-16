package mx.upax.formacion.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.upax.formacion.R
import com.upax.formacion.databinding.ItemFormationNewBinding
import com.upax.formacion.databinding.ItemMenuBinding
import mx.arquidiocesis.eamxcommonutils.util.*
import mx.upax.formacion.model.BaseModel
import mx.upax.formacion.model.ItemMenu
import mx.upax.formacion.model.MenuModel
import mx.upax.formacion.ui.PDF
import mx.upax.formacion.ui.WEB
import mx.upax.formacion.ui.YOUTUBE
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.regex.Pattern

class MenuAdapter(
    private val menuList: MutableList<ItemMenu>,
    private val listener: (ItemMenu?) -> Unit
) :
    RecyclerView.Adapter<MenuAdapter.ChurchViewHolder>() {

    var itemSelect : ItemMenu? = null
    var indexItemSelect : Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChurchViewHolder {
        return ChurchViewHolder(ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChurchViewHolder, position: Int) {
        holder.bind(holder.binding.root.context , menuList[position]){
            itemSelect?.let { item ->
                if(indexItemSelect != position) {
                    menuList.forEach { menuItem -> menuItem.pressed = false }
                    notifyItemChanged(indexItemSelect)

                    it.pressed = true
                    menuList[position] = it
                    notifyItemChanged(position)

                    indexItemSelect = position
                    itemSelect = it
                }
            } ?: kotlin.run {
                menuList[position] = it
                itemSelect = it
                indexItemSelect = position
                notifyItemChanged(position)
            }

            listener(itemSelect)
        }

    }

    override fun getItemCount(): Int = menuList.size

    fun setDefualtFirstTabSelected() {
        menuList[0].pressed = true
        itemSelect = menuList[0]
        indexItemSelect = 0
        notifyItemChanged(0)
        listener(itemSelect)
    }

    class ChurchViewHolder(val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            item: ItemMenu,
            listener: (ItemMenu) -> Unit
        ) {
            binding.ivMenuItemUnselect.loadByUrlSampleSquare(context,item.icon_url)
            binding.ivMenuItemSelect.loadByUrlSampleSquare(context,item.icon_pressed_url)

            if(item.pressed){
                binding.ivMenuItemSelect.visibility = View.VISIBLE
                binding.ivMenuItemUnselect.visibility = View.GONE
                binding.ivMenuItemSelectBottom.visibility = View.VISIBLE
            }else{
                binding.ivMenuItemSelect.visibility = View.GONE
                binding.ivMenuItemUnselect.visibility = View.VISIBLE
                binding.ivMenuItemSelectBottom.visibility = View.GONE
            }

            binding.llItem.setOnClickListener{
                listener(item)
            }
        }
    }
}