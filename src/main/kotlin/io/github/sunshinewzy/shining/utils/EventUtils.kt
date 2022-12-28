package io.github.sunshinewzy.shining.utils

import org.bukkit.event.block.Action

fun Action.isRightClick(): Boolean =
    this == Action.RIGHT_CLICK_AIR || this == Action.RIGHT_CLICK_BLOCK

fun Action.isLeftClick(): Boolean =
    this == Action.LEFT_CLICK_AIR || this == Action.LEFT_CLICK_BLOCK

fun Action.isClickBlock(): Boolean =
    this == Action.LEFT_CLICK_BLOCK || this == Action.RIGHT_CLICK_BLOCK

fun Action.isClickAir(): Boolean =
    this == Action.LEFT_CLICK_AIR || this == Action.RIGHT_CLICK_AIR
