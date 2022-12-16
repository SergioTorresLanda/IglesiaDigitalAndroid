package mx.arquidiocesis.eamxprofilemodule.ui.promises.createpromise.adapter.item_decoration

import android.graphics.Rect
import android.view.View
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView

class EAMXSaintItemDecoration(
    private val horizontalMargin: Int = 0,
    private val adjacentVisibleSize: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val childCount = state.itemCount

        val firstItem = position == 0
        val lastItem = position == childCount - 1

        var itemWidth = parent.width - 2 * adjacentVisibleSize - 0 * horizontalMargin

        if (firstItem || lastItem) {
            itemWidth = parent.width - adjacentVisibleSize - 25 * horizontalMargin
        }

        if (firstItem && lastItem) {
            itemWidth = parent.width - 2 * horizontalMargin
        }

        //  For Fix error to get recycler width, solve get screen width
        if (itemWidth < 0) {
            itemWidth = 800
        }
        
        view.updateLayoutParams {
            width = itemWidth
            height = RecyclerView.LayoutParams.MATCH_PARENT
        }

        with(outRect) {
            left = if (firstItem) 25 * horizontalMargin else horizontalMargin
            right = if (lastItem) 25 * horizontalMargin else horizontalMargin
            top = 50
            bottom = 50
        }
    }
}