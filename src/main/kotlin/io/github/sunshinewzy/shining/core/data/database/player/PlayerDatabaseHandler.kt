package io.github.sunshinewzy.shining.core.data.database.player

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object PlayerDatabaseHandler {

    val dataContainer: MutableMap<UUID, PlayerDataContainer> = ConcurrentHashMap()


    fun Player.getDataContainer(): PlayerDataContainer {
        return dataContainer[uniqueId] ?: error("unavailable")
    }

    fun Player.setupDataContainer() {
        dataContainer[uniqueId] = PlayerDataContainer(uniqueId.toString())
    }

    fun Player.releaseDataContainer() {
        dataContainer.remove(uniqueId)
    }

    fun UUID.executePlayerDataContainer(action: (PlayerDataContainer) -> Unit) {
        dataContainer[this]?.let(action)
            ?: PlayerDataContainer(this.toString()).let {
                action(it)
                dataContainer.remove(this)
            }
    }

}