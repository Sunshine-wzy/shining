package io.github.sunshinewzy.shining.core.guide.context

import io.github.sunshinewzy.shining.api.guide.context.EmptyGuideContext
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.context.GuideContext.Element
import io.github.sunshinewzy.shining.api.guide.context.GuideContext.Key
import java.util.function.BiFunction

/**
 * Base class for [GuideContext.Element] implementations.
 */
abstract class AbstractGuideContextElement(override val key: Key<*>) : AbstractGuideContext(), Element {
    @Suppress("UNCHECKED_CAST")
    override fun <E : Element> get(key: Key<E>): E? =
        if (this.key == key) this as E else null

    override fun <R> fold(initial: R, operation: BiFunction<R, Element, R>): R =
        operation.apply(initial, this)

    override fun minusKey(key: Key<*>): GuideContext =
        if (this.key == key) EmptyGuideContext else this
}

abstract class AbstractGuideContext : GuideContext {
    override fun plus(context: GuideContext): GuideContext =
        if (context === EmptyGuideContext) this
        else context.fold(this as GuideContext) { acc, element ->
            val removed = acc.minusKey(element.key)
            if (removed === EmptyGuideContext) element
            else CombinedGuideContext(removed, element)
        }
}

internal class CombinedGuideContext(
    private val left: GuideContext,
    private val element: Element
) : AbstractGuideContext() {
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

    override fun <R> fold(initial: R, operation: BiFunction<R, Element, R>): R =
        operation.apply(left.fold(initial, operation), element)

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