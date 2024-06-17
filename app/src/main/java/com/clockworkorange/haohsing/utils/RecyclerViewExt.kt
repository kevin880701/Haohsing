package com.clockworkorange.haohsing.utils

import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

//https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview
fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
    val divider = DividerItemDecoration(
        this.context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}

//<inset xmlns:android="http://schemas.android.com/apk/res/android"
//    android:insetLeft="10dp"
//    android:insetRight="10dp">
//
//    <shape>
//        <size android:height="0.5dp" />
//        <solid android:color="@android:color/darker_gray" />
//    </shape>
//
//</inset>

fun RecyclerView.scrollToBottom() {
    adapter?.itemCount?.let { smoothScrollToPosition(it) }
}