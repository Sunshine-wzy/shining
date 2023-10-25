package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.core.guide.GuideClipboardSession.Mode.COPY
import io.github.sunshinewzy.shining.core.guide.GuideClipboardSession.Mode.CUT
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import org.bukkit.entity.Player
import java.util.*

object ShiningGuideClipboard {
    
    private val clipboardMap: MutableMap<UUID, GuideClipboardSession> = HashMap()
    
    fun copy(player: Player, session: GuideClipboardSession) {
        clipboardMap[player.uniqueId] = session
    }
    
    fun paste(player: Player): GuideClipboardSession? {
        val session = clipboardMap[player.uniqueId] ?: return null
        when (session.mode) {
            COPY -> {}
            CUT -> {
                session.container?.let { container ->
                    container.unregisterElement(session.element.getId(), cascade = false, remove = false)
                    ShiningDispatchers.launchDB {
                        GuideElementRegistry.saveElement(container)
                    }
                }
                clipboardMap -= player.uniqueId
            }
        }
        session.pasteCallback.accept(session)
        return session
    }
    
    fun hasClipboard(player: Player): Boolean =
        clipboardMap.containsKey(player.uniqueId)
    
    fun clearClipboard(player: Player) {
        clipboardMap -= player.uniqueId
    }
    
}