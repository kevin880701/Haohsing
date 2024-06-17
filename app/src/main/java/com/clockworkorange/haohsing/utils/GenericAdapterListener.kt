package com.clockworkorange.haohsing.utils

fun interface GenericAdapterListener<T> {
    fun onItemClick(item: T)
}