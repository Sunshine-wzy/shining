package io.github.sunshinewzy.shining.objects.item

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.Itemable
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.lang.item.LanguageItem
import io.github.sunshinewzy.shining.core.lang.item.LocalizedItem
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.objects.SItem
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

enum class ShiningIcon(val item: ItemStack) : Itemable {
    BACK_MENU(NamespacedIdItem(Material.ENCHANTED_BOOK, NamespacedId(Shining, "icon-back_menu"))),
    BACK(NamespacedIdItem(Material.ENCHANTED_BOOK, NamespacedId(Shining, "icon-back"))),
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
    ADD_MODE(NamespacedIdItem(Material.SLIME_BALL, NamespacedId(Shining, "icon-add_mode"))),
    SEARCH(NamespacedIdItem(Material.COMPASS, NamespacedId(Shining, "icon-search"))),
    CONFIRM(NamespacedIdItem(Material.SLIME_BALL, NamespacedId(Shining, "icon-confirm"))),
    CANCEL(NamespacedIdItem(Material.BARRIER, NamespacedId(Shining, "icon-cancel"))),
    SETTINGS(NamespacedIdItem(Material.ENDER_EYE, NamespacedId(Shining, "icon-settings"))),
    REMOVE(NamespacedIdItem(Material.BARRIER, NamespacedId(Shining, "icon-remove"))),
    SELECT_MODE(NamespacedIdItem(Material.ARROW, NamespacedId(Shining, "icon-select_mode"))),
    CONSUME_MODE(NamespacedIdItem(Material.APPLE, NamespacedId(Shining, "icon-consume_mode"))),
    IS_CONSUME(NamespacedIdItem(Material.APPLE, NamespacedId(Shining, "icon-is_consume"))),
    SUBMIT_FAILURE(NamespacedIdItem(Material.BARRIER, NamespacedId(Shining, "icon-submit_failure"))),
    MOVE_RIGHT(NamespacedIdItem(Material.LIME_STAINED_GLASS_PANE, NamespacedId(Shining, "icon-move_right"))),
    MOVE_LEFT(NamespacedIdItem(Material.LIME_STAINED_GLASS_PANE, NamespacedId(Shining, "icon-move_left"))),
    MOVE_UP(NamespacedIdItem(Material.LIME_STAINED_GLASS_PANE, NamespacedId(Shining, "icon-move_up"))),
    MOVE_DOWN(NamespacedIdItem(Material.LIME_STAINED_GLASS_PANE, NamespacedId(Shining, "icon-move_down"))),
    
    
    ;


    override fun getItemStack(): ItemStack = item

    fun getNamespacedIdItem(): NamespacedIdItem = item as NamespacedIdItem
    
    fun getLanguageItem(): LanguageItem = item as LanguageItem
    
    fun toLocalizedItem(sender: CommandSender): LocalizedItem = getLanguageItem().toLocalizedItem(sender)
    
    fun toStateItem(state: String): LanguageItem = getLanguageItem().toStateItem(state)
    
    fun toStateLocalizedItem(state: String, sender: CommandSender): LocalizedItem = toStateItem(state).toLocalizedItem(sender)
    
    fun toStateShinyLocalizedItem(state: String, sender: CommandSender): LocalizedItem = toStateItem(state).shiny().toLocalizedItem(sender)
    
}