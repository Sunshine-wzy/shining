package io.github.sunshinewzy.shining.api.guide

import io.github.sunshinewzy.shining.api.guide.GuideContext.Element
import io.github.sunshinewzy.shining.api.guide.GuideContext.Key
import io.github.sunshinewzy.shining.core.guide.CombinedGuideContext
import io.github.sunshinewzy.shining.core.guide.EmptyGuideContext

/**
 * Persistent context for shining guide. It is an indexed set of [Element] instances.
 * An indexed set is a mix between a set and a map.
 * Every element in this set has a unique [Key].
 */
interface GuideContext {
    /**
     * Returns the element with the given [key] from this context or `null`.
     */
    operator fun <E : Element> get(key: Key<E>): E?

    /**
     * Accumulates entries of this context starting with [initial] value and applying [operation]
     * from left to right to current accumulator value and each element of this context.
     */
    fun <R> fold(initial: R, operation: (R, Element) -> R): R

    /**
     * Returns a context containing elements from this context and elements from  other [context].
     * The elements from this context with the same key as in the other one are dropped.
     */
    operator fun plus(context: GuideContext): GuideContext =
        if (context === EmptyGuideContext) this
        else context.fold(this) { acc, element ->
            val removed = acc.minusKey(element.key)
            if (removed === EmptyGuideContext) element
            else CombinedGuideContext(removed, element)
        }

    /**
     * Returns a context containing elements from this context, but without an element with
     * the specified [key].
     */
    fun minusKey(key: Key<*>): GuideContext

    /**
     * Key for the elements of GuideContext. E is a type of element with this key.
     */
    interface Key<E : Element>

    /**
     * An element of the [GuideContext]. An element of the guide context is a singleton context by itself.
     */
    interface Element : GuideContext {
        val key: Key<*>

        @Suppress("UNCHECKED_CAST")
        override fun <E : Element> get(key: Key<E>): E? =
            if (this.key == key) this as E else null

        override fun <R> fold(initial: R, operation: (R, Element) -> R): R =
            operation(initial, this)

        override fun minusKey(key: Key<*>): GuideContext =
            if (this.key == key) EmptyGuideContext else this
    }
}