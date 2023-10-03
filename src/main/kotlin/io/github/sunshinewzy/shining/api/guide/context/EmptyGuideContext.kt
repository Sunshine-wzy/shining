package io.github.sunshinewzy.shining.api.guide.context

import java.util.function.BiFunction

object EmptyGuideContext : GuideContext {
    override fun <E : GuideContext.Element> get(key: GuideContext.Key<E>): E? = null

    override fun <R> fold(initial: R, operation: BiFunction<R, GuideContext.Element, R>): R = initial

    override fun plus(context: GuideContext): GuideContext = context

    override fun minusKey(key: GuideContext.Key<*>): GuideContext = this

    override fun hashCode(): Int = 0

    override fun toString(): String = "EmptyGuideContext"
}