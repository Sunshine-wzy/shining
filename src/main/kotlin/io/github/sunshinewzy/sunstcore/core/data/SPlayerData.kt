package io.github.sunshinewzy.sunstcore.core.data

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

abstract class SPlayerData : SAutoSaveData {
    constructor(plugin: JavaPlugin, uuid: String, path: String = "SPlayer", saveTime: Long = 12_000): super(plugin, uuid, path, saveTime)
    constructor(plugin: JavaPlugin, uuid: UUID, path: String = "SPlayer", saveTime: Long = 12_000): this(plugin, uuid.toString(), path, saveTime)
    constructor(plugin: JavaPlugin, player: Player, path: String = "SPlayer", saveTime: Long = 12_000): this(plugin, player.uniqueId, path, saveTime)
    constructor(plugin: JavaPlugin, uuid: String, file: File): super(plugin, uuid, file)
}
