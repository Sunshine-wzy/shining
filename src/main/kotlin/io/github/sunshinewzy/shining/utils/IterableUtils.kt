package io.github.sunshinewzy.shining.utils

fun <E> MutableIterable<E>.poll(): E? {
    val iterator = iterator()
    return if (iterator.hasNext()) {
        val element = iterator.next()
        iterator.remove()
        element
    } else null
}