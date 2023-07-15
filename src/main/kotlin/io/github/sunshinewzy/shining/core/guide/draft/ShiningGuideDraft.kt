package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ShiningGuideDraft {
    
    private val playerLastOpenFolderMap: MutableMap<UUID, GuideDraftFolder> = ConcurrentHashMap()
    private val playerSelectModeSet: MutableSet<UUID> = ConcurrentHashMap.newKeySet()
    
    
    fun openMainMenu(player: Player) {
        playerLastOpenFolderMap -= player.uniqueId
        
        Shining.launchIO { 
            GuideDraftFolder.getMainFolder().open(player)
        }
    }
    
    fun openLastMenu(player: Player) {
        playerLastOpenFolderMap[player.uniqueId]?.let { 
            Shining.launchIO {
                it.open(player)
            }
            return
        }
        
        openMainMenu(player)
    }
    
    fun openMainSaveMenu(player: Player, state: IGuideElementState) {
        playerLastOpenFolderMap -= player.uniqueId
        
        Shining.launchIO { 
            GuideDraftFolder.getMainFolder().openSaveMenu(player, state)
        }
    }
    
    fun openLastSaveMenu(player: Player, state: IGuideElementState) {
        playerLastOpenFolderMap[player.uniqueId]?.let {
            Shining.launchIO {
                it.openSaveMenu(player, state)
            }
            return
        }

        openMainSaveMenu(player, state)
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