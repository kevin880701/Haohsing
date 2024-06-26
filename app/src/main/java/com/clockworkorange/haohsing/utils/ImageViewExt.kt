package com.clockworkorange.haohsing.utils

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImage(url: String?) {
    url ?: return
    Glide.with(this)
        .load(url)
        .into(this)
}