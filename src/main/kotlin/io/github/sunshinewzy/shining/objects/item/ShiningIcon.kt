package io.github.sunshinewzy.shining.objects.item

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.Itemable
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.objects.SItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.buildItem

enum class ShiningIcon(val item: ItemStack) : Itemable {
    BACK(NamespacedIdItem(Material.ENCHANTED_BOOK, NamespacedId(Shining, "icon-back"))),
    BACK_LAST_PAGE(NamespacedIdItem(Material.ENCHANTED_BOOK, NamespacedId(Shining, "icon-back_last_page"))),
    SUBMIT(NamespacedIdItem(Material.SLIME_BALL, NamespacedId(Shining, "icon-submit"))),
    WORKBENCH(NamespacedIdItem(Material.CRAFTING_TABLE, NamespacedId(Shining, "icon-workbench"))),
    PAGE_NEXT(NamespacedIdItem(Material.ENCHANTED_BOOK, NamespacedId(Shining, "icon-page_next"))),
    PAGE_PREVIOUS(NamespacedIdItem(Material.ENCHANTED_BOOK, NamespacedId(Shining, "icon-page_previous"))),
    HOME(NamespacedIdItem(Material.COMPASS, NamespacedId(Shining, "icon-home"))),
    EDGE(SItem(Material.GRAY_STAINED_GLASS_PANE, " ")),
    EDGE_GLASS_PANE(SItem(Material.GLASS_PANE, " ")),
    PAGE_NEXT_GLASS_PANE(NamespacedIdItem(Material.LIME_STAINED_GLASS_PANE, NamespacedId(Shining, "icon-page_next_glass_pane"))),
    PAGE_PREVIOUS_GLASS_PANE(NamespacedIdItem(Material.LIME_STAINED_GLASS_PANE, NamespacedId(Shining, "icon-page_previous_glass_pane"))),
    RENAME(NamespacedIdItem(Material.NAME_TAG, NamespacedId(Shining, "icon-rename"))),
    EDIT_LORE(NamespacedIdItem(Material.EMERALD, NamespacedId(Shining, "icon-edit_lore"))),
    REMOVE_MODE(NamespacedIdItem(Material.BARRIER, NamespacedId(Shining, "icon-remove_mode"))),
    REMOVE_MODE_SHINY(NamespacedIdItem(buildItem(Material.BARRIER) { shiny() }, NamespacedId(Shining, "icon-remove_mode_shiny"))),
    ADD_MODE(NamespacedIdItem(Material.SLIME_BALL, NamespacedId(Shining, "icon-add_mode"))),
    ADD_MODE_SHINY(NamespacedIdItem(buildItem(Material.SLIME_BALL) { shiny() }, NamespacedId(Shining, "icon-add_mode_shiny"))),
    SEARCH(NamespacedIdItem(Material.COMPASS, NamespacedId(Shining, "icon-search"))),
    CONFIRM(NamespacedIdItem(Material.SLIME_BALL, NamespacedId(Shining, "icon-confirm"))),
    CANCEL(NamespacedIdItem(Material.BARRIER, NamespacedId(Shining, "icon-cancel"))),
    SETTINGS(NamespacedIdItem(Material.ENDER_EYE, NamespacedId(Shining, "icon-settings")))
    
    ;


    override fun getItemStack(): ItemStack = item
    
    fun getNamespacedIdItem(): NamespacedIdItem = item as NamespacedIdItem
    
}