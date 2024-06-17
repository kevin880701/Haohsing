package com.clockworkorange.haohsing.ui.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(
    private val top: Int = 0,
    private val left: Int = 0,
    private val right: Int = 0,
    private val bottom: Int = 0,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = left
        outRect.right = right
        outRect.bottom = bottom

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = top
        }
    }
}