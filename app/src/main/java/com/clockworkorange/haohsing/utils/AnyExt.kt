package com.clockworkorange.haohsing.utils

fun Any?.toDisplay(nullDisplay: String, orDisplay: String): String {
    return if (this == null) nullDisplay else orDisplay
}