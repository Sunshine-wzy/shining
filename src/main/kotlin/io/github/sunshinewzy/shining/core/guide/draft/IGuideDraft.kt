package io.github.sunshinewzy.shining.core.guide.draft

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface IGuideDraft {
    
    fun getSymbol(player: Player): ItemStack

    suspend fun open(player: Player, previousFolder: GuideDraftFolder? = null)
    
    suspend fun delete(previousFolder: GuideDraftFolder)
    
    suspend fun move(previousFolder: GuideDraftFolder, newFolder: GuideDraftFolder)
    
}