package io.github.sunshinewzy.sunstcore.core.data.legacy.internal

import io.github.sunshinewzy.sunstcore.core.data.DataManager
import io.github.sunshinewzy.sunstcore.core.data.DataManager.getMap
import io.github.sunshinewzy.sunstcore.core.data.legacy.SPlayerData
import io.github.sunshinewzy.sunstcore.core.task.TaskProgress
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

class SunSTPlayerData : SPlayerData {
    constructor(plugin: JavaPlugin, uuid: String, path: String = "SPlayer", saveTime: Long = 12_000): super(plugin, uuid, path, saveTime)
    constructor(plugin: JavaPlugin, uuid: UUID, path: String = "SPlayer", saveTime: Long = 12_000): this(plugin, uuid.toString(), path, saveTime)
    constructor(plugin: JavaPlugin, player: Player, path: String = "SPlayer", saveTime: Long = 12_000): this(plugin, player.uniqueId, path, saveTime)
    constructor(plugin: JavaPlugin, uuid: String, file: File): super(plugin, uuid, file)

    init {
        DataManager.sPlayerData[name] = this
    }
    
    val taskProgress = HashMap<String, TaskProgress>()
    val isFirstJoinGive = HashMap<String, Boolean>()


    override fun YamlConfiguration.createConfig() {
        
    }

    override fun YamlConfiguration.modifyConfig() {
        set("taskProgress", taskProgress)
        set("isFirstJoinGive", isFirstJoinGive)
        
        if(data.containsKey(name)) {
            data[name]?.let { 
                set("playerData", it)
            }
        }
        
    }

    override fun YamlConfiguration.loadConfig() {
        getMap("taskProgress", taskProgress)
        getMap("isFirstJoinGive", isFirstJoinGive)
        
        val playerDataMap = HashMap<String, String>()
        if(getMap("playerData", playerDataMap) && playerDataMap.isNotEmpty())
            addData(name, playerDataMap)
        
    }
    
    
    companion object {
        // PlayerData <uuid, <key, value>>
        val data = HashMap<String, HashMap<String, String>>()

        @JvmStatic
        fun addData(uuid: String, key: String, value: String) {
            if(data.containsKey(uuid)) {
                data[uuid]?.let { playerData ->
                    playerData[key] = value
                }
            } else {
                data[uuid] = hashMapOf(key to value)
            }
        }

        @JvmStatic
        fun addData(player: Player, key: String, value: String) {
            addData(player.uniqueId.toString(), key, value)
        }

        @JvmStatic
        fun addData(uuid: String, dataMap: HashMap<String, String>) {
            if(data.containsKey(uuid)) {
                data[uuid]?.putAll(dataMap)
            } else {
                data[uuid] = dataMap
            }
        }

        @JvmStatic
        fun addData(player: Player, dataMap: HashMap<String, String>) {
            addData(player.uniqueId.toString(), dataMap)
        }

        @JvmStatic
        fun removeData(uuid: String, key: String) {
            if(data.containsKey(uuid)) {
                data[uuid]?.remove(key)
            }
        }

        @JvmStatic
        fun removeData(player: Player, key: String) {
            removeData(player.uniqueId.toString(), key)
        }

        @JvmStatic
        fun getData(uuid: String, key: String): String? {
            if(data.containsKey(uuid)) {
                data[uuid]?.let { playerData ->
                    if(playerData.containsKey(key)) {
                        return playerData[key]
                    }
                }
            }
            return null
        }

        @JvmStatic
        fun getData(player: Player, key: String): String? =
            getData(player.uniqueId.toString(), key)

        @JvmStatic
        fun getDataOrFail(uuid: String, key: String): String {
            if(data.containsKey(uuid)) {
                data[uuid]?.let { playerData ->
                    if(playerData.containsKey(key)) {
                        return playerData[key] ?: throw IllegalArgumentException("The Player '$uuid' doesn't have data of $key.")
                    }
                }
            }
            
            throw IllegalArgumentException("The Player '$uuid' doesn't have data of $key.")
        }

        @JvmStatic
        fun getDataOrFail(player: Player, key: String): String =
            getDataOrFail(player.uniqueId.toString(), key)
    }
    
}