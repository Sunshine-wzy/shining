package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.GuideContext.Element
import io.github.sunshinewzy.shining.api.guide.GuideContext.Key

object EmptyGuideContext : GuideContext {
    override fun <E : Element> get(key: Key<E>): E? = null

    override fun <R> fold(initial: R, operation: (R, Element) -> R): R = initial

    override fun plus(context: GuideContext): GuideContext = context

    override fun minusKey(key: Key<*>): GuideContext = this

    override fun hashCode(): Int = 0

    override fun toString(): String = "EmptyGuideContext"
}

internal class CombinedGuideContext(
    private val left: GuideContext,
    private val element: Element
) : GuideContext {
    override fun <E : Element> get(key: Key<E>): E? {
        var cur = this
        while (true) {
            cur.element[key]?.let { return it }
            val next = cur.left
            if (next is CombinedGuideContext) {
                cur = next
            } else {
                return next[key]
            }
        }
    }

    override fun <R> fold(initial: R, operation: (R, Element) -> R): R =
        operation(left.fold(initial, operation), element)

    override fun minusKey(key: Key<*>): GuideContext {
        element[key]?.let { return left }
        val newLeft = left.minusKey(key)
        return when {
            newLeft === left -> this
            newLeft === EmptyGuideContext -> element
            else -> CombinedGuideContext(newLeft, element)
        }
    }


    private fun size(): Int {
        var cur = this
        var size = 2
        while (true) {
            cur = cur.left as? CombinedGuideContext ?: return size
            size++
        }
    }

    private fun contains(element: Element): Boolean =
        get(element.key) == element

    private fun containsAll(context: CombinedGuideContext): Boolean {
        var cur = context
        while (true) {
            if (!contains(cur.element)) return false
            val next = cur.left
            if (next is CombinedGuideContext) {
                cur = next
            } else {
                return contains(next as Element)
            }
        }
    }


    override fun equals(other: Any?): Boolean =
        this === other || other is CombinedGuideContext && other.size() == size() && other.containsAll(this)

    override fun hashCode(): Int = left.hashCode() + element.hashCode()

    override fun toString(): String =
        "[" + fold("") { acc, element ->
            if (acc.isEmpty()) element.toString() else "$acc, $element"
        } + "]"
}