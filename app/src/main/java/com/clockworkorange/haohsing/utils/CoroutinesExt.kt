package com.clockworkorange.haohsing.utils

import kotlinx.coroutines.channels.SendChannel

/**
 * Tries to send an element to a Channel and ignores the exception.
 */
fun <E> SendChannel<E>.tryOffer(element: E): Boolean = try {
    trySend(element).isSuccess
} catch (t: Throwable) {
    false // Ignore
}