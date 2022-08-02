package io.github.sunshinewzy.sunstcore.modules.menu

import org.bukkit.entity.Player
import taboolib.module.ui.type.Linked

open class Select<T>(title: String) : Linked<T>(title) {
    
    fun searchMap(searchMap: (player: Player, elements: List<T>) -> Map<String, T>) {
        
    }
    
}