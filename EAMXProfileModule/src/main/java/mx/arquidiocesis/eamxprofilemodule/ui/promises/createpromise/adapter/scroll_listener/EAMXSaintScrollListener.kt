package mx.arquidiocesis.eamxprofilemodule.ui.fragments.promises.createpromise.adapter.scroll_listener

import android.app.ActionBar
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.arquidiocesis.eamxprofilemodule.databinding.EamxProfilePromiseItemSaintBinding
import kotlin.math.roundToInt

class EAMXSaintScrollListener : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

        val itemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        val lastIndex = recyclerView.adapter?.itemCount!!

        if (itemPosition >= 0) {

            //Item Prev
            if (itemPosition > 0 ) {
                layoutManager.findViewByPosition(itemPosition -1 )?.let {
                    val itemBinding = EamxProfilePromiseItemSaintBinding.bind(it)
                    setThumbnailDimen(itemBinding.imgSaint, recyclerView.width * 0.6,recyclerView.width*0.4)
                }
            }

            // Next Item
            if (itemPosition < lastIndex) {
                layoutManager.findViewByPosition(itemPosition + 1)?.let {
                    val itemBinding = EamxProfilePromiseItemSaintBinding.bind(it)
                    setThumbnailDimen(itemBinding.imgSaint, recyclerView.width * 0.6, recyclerView.width*0.4)
                }
            }

            //Current Item
            layoutManager.findViewByPosition(itemPosition)?.let {
                val itemBinding = EamxProfilePromiseItemSaintBinding.bind(it)
                setFullImage(itemBinding.imgSaint, recyclerView.height * 0.9, recyclerView.width * 0.5)
            }

        }
    }

    private fun setFullImage(image: ImageView, d: Double, d1: Double ) {
        image.updateLayoutParams {
            height = d.roundToInt()
            width = d1.roundToInt()
        }
    }

    private fun setThumbnailDimen(image: ImageView, size: Double, size2: Double) {
        image.updateLayoutParams {
            height = size.roundToInt()
            width = size2.roundToInt()
           // width = RecyclerView.LayoutParams.WRAP_CONTENT+2

        }
    }
}