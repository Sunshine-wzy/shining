package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ShiningGuideDraft {
    
    private val playerLastOpenFolderMap: MutableMap<UUID, GuideDraftFolder> = ConcurrentHashMap()
    private val playerSelectModeSet: MutableSet<UUID> = ConcurrentHashMap.newKeySet()
    
    
    fun openMainMenu(player: Player) {
        playerLastOpenFolderMap -= player.uniqueId
        
        ShiningDispatchers.launchDB { 
            GuideDraftFolder.getMainFolder().open(player)
        }
    }
    
    fun openLastMenu(player: Player) {
        playerLastOpenFolderMap[player.uniqueId]?.let { 
            ShiningDispatchers.launchDB {
                it.open(player)
            }
            return
        }
        
        openMainMenu(player)
    }
    
    fun openMainSelectMenu(player: Player, context: GuideContext) {
        playerLastOpenFolderMap -= player.uniqueId
        
        ShiningDispatchers.launchDB { 
            GuideDraftFolder.getMainFolder().openSelectMenu(player, context)
        }
    }
    
    fun openLastSelectMenu(player: Player, context: GuideContext) {
        playerLastOpenFolderMap[player.uniqueId]?.let {
            ShiningDispatchers.launchDB {
                it.openSelectMenu(player, context)
            }
            return
        }

        openMainSelectMenu(player, context)
    }
    
    fun recordLastOpenFolder(uuid: UUID, folder: GuideDraftFolder) {
        playerLastOpenFolderMap[uuid] = folder
    }
    
    fun recordLastOpenFolder(player: Player, folder: GuideDraftFolder) {
        recordLastOpenFolder(player.uniqueId, folder)
    }
    
    fun isPlayerSelectModeEnabled(player: Player): Boolean =
        playerSelectModeSet.contains(player.uniqueId)
    
    fun enablePlayerSelectMode(player: Player): Boolean {
        playerSelectModeSet += player.uniqueId
        return true
    }
    
    fun disablePlayerSelectMode(player: Player): Boolean {
        playerSelectModeSet -= player.uniqueId
        return false
    }
    
    fun switchPlayerSelectMode(player: Player): Boolean =
        if (isPlayerSelectModeEnabled(player)) disablePlayerSelectMode(player)
        else enablePlayerSelectMode(player)
    
}