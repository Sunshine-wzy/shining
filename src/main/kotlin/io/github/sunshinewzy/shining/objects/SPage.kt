package io.github.sunshinewzy.shining.objects

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


data class SPage(
    val page: Int,
    var action: Inventory.() -> Unit = {},
    val items: HashMap<Int, ItemStack> = hashMapOf(),
    val buttons: HashMap<Int, Triple<String, ItemStack, InventoryClickEvent.() -> Unit>> = hashMapOf(),
    val turnPageButtons: HashMap<Int, Pair<STurnPageType, ItemStack>> = hashMapOf()
)


data class SPageValue(var page: Int)


enum class STurnPageType {
    NEXT_PAGE,
    PRE_PAGE
}