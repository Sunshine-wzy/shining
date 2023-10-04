package io.github.sunshinewzy.shining.api.lang.item

import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

interface ILanguageItem : ILocalizedItem {
    
    fun shiny(): ILanguageItem

    fun toLocalizedItem(sender: CommandSender): ILocalizedItem
    
    fun toLocalizedItemStack(sender: CommandSender): ItemStack =
        toLocalizedItem(sender).getItemStack()

    fun toStateItem(state: String): ILanguageItem
    
}