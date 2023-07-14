package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.Shining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ShiningGuideDraft {
    
    private val playerLastOpenFolderMap: MutableMap<UUID, GuideDraftFolder> = ConcurrentHashMap()
    
    
    fun openMainMenu(player: Player) {
        playerLastOpenFolderMap -= player.uniqueId
        
        Shining.scope.launch(Dispatchers.IO) { 
            GuideDraftFolder.getMainFolder().open(player)
        }
    }
    
    fun openLastDraft(player: Player) {
        playerLastOpenFolderMap[player.uniqueId]?.let { 
            Shining.scope.launch(Dispatchers.IO) {
                it.open(player)
            }
            return
        }
        
        openMainMenu(player)
    }
    
    fun recordLastOpenFolder(uuid: UUID, folder: GuideDraftFolder) {
        playerLastOpenFolderMap[uuid] = folder
    }
    
    fun recordLastOpenFolder(player: Player, folder: GuideDraftFolder) {
        recordLastOpenFolder(player.uniqueId, folder)
    }
    
}