package io.github.sunshinewzy.shining.utils

import org.bukkit.event.block.Action
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.ProxyListener
import java.io.Closeable

fun Action.isRightClick(): Boolean =
    this == Action.RIGHT_CLICK_AIR || this == Action.RIGHT_CLICK_BLOCK

fun Action.isLeftClick(): Boolean =
    this == Action.LEFT_CLICK_AIR || this == Action.LEFT_CLICK_BLOCK

fun Action.isClickBlock(): Boolean =
    this == Action.LEFT_CLICK_BLOCK || this == Action.RIGHT_CLICK_BLOCK

fun Action.isClickAir(): Boolean =
    this == Action.LEFT_CLICK_AIR || this == Action.RIGHT_CLICK_AIR


/**
 * Register a Bukkit listener
 *
 * @param ignoreCancelled Whether to ignore the cancelled event
 * @param func Event handling function
 * @return Listener
 */
inline fun <reified T> registerBukkitListener(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = true,
    noinline func: Closeable.(T) -> Unit
): ProxyListener = taboolib.common.platform.function.registerBukkitListener(T::class.java, priority, ignoreCancelled, func)