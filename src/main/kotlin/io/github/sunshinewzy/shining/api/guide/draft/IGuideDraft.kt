package io.github.sunshinewzy.shining.api.guide.draft

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface IGuideDraft {
    
    fun getSymbol(player: Player): ItemStack
    
}