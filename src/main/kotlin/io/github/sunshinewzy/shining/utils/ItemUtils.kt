package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.api.Itemable
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.buildItem

fun buildItem(item: Itemable, builder: ItemBuilder.() -> Unit = {}): ItemStack {
    return buildItem(item.getItemStack(), builder)
}