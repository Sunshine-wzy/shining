package io.github.sunshinewzy.sunstcore.core.data.legacy.internal

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.core.data.DataManager.getMap
import io.github.sunshinewzy.sunstcore.core.data.legacy.SAutoCoverSaveData
import io.github.sunshinewzy.sunstcore.events.slocationdata.SLocationDataAddEvent
import io.github.sunshinewzy.sunstcore.events.slocationdata.SLocationDataClearEvent
import io.github.sunshinewzy.sunstcore.events.slocationdata.SLocationDataRemoveEvent
import io.github.sunshinewzy.sunstcore.interfaces.Initable
import io.github.sunshinewzy.sunstcore.objects.SLocation
import io.github.sunshinewzy.sunstcore.utils.subscribeEvent
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.world.WorldLoadEvent

class SLocationData(val world: String) : SAutoCoverSaveData(SunSTCore.plugin, world, "SLocationData") {

    override fun YamlConfiguration.createConfig() {
        
    }

    override fun YamlConfiguration.modifyConfig() {
        if(data.containsKey(world)) {
            data[world]?.forEach { (sLoc, data) ->
                set(sLoc, data)
            }
        }
    }

    override fun YamlConfiguration.loadConfig() {
        val roots = getKeys(false)
        roots.forEach { sLoc ->
            val map = HashMap<String, String>()

            if(getMap(sLoc, map) && map.isNotEmpty())
                addData(world, sLoc, map)
        }
    }
    
    
    companion object : Initable {
        private val createdDataWorlds = HashSet<String>()
        
        // SLocationData <world, <sLocation, <key, value>>>
        val data = HashMap<String, HashMap<String, HashMap<String, String>>>()


        fun addData(world: String, sLocation: String, key: String, value: String) {
            if(data.containsKey(world)) {
                data[world]?.let { worldData ->
                    if(worldData.containsKey(sLocation)) {
                        worldData[sLocation]?.let {
                            it[key] = value
                        }
                    } else worldData[sLocation] = hashMapOf(key to value)
                }
            } else {
                checkWorldData(world)
                
                val map = HashMap<String, HashMap<String, String>>()
                map[sLocation] = hashMapOf(key to value)
                data[world] = map
            }

            SunSTCore.pluginManager.callEvent(SLocationDataAddEvent(SLocation(sLocation), key, value))
        }

        fun addData(world: String, sLocation: String, dataMap: HashMap<String, String>) {
            if(data.containsKey(world)) {
                data[world]?.let { worldData ->
                    if(worldData.containsKey(sLocation)) {
                        worldData[sLocation]?.putAll(dataMap)
                    } else worldData[sLocation] = dataMap
                }
            } else {
                checkWorldData(world)
                
                val map = HashMap<String, HashMap<String, String>>()
                map[sLocation] = dataMap
                data[world] = map
            }
        }

        fun removeData(world: String, sLocation: String, key: String) {
            if(data.containsKey(world)) {
                data[world]?.let { worldData ->
                    if(worldData.containsKey(sLocation)) {
                        worldData[sLocation]?.remove(key)
                    }
                }
            }
            
            SunSTCore.pluginManager.callEvent(SLocationDataRemoveEvent(SLocation(sLocation), key))
        }

        fun clearData(world: String, sLocation: String) {
            if(data.containsKey(world)) {
                data[world]?.remove(sLocation)
            }
            
            SunSTCore.pluginManager.callEvent(SLocationDataClearEvent(SLocation(sLocation)))
        }

        fun getData(world: String, sLocation: String, key: String): String? {
            if(data.containsKey(world)) {
                data[world]?.let { worldData ->
                    if(worldData.containsKey(sLocation)) {
                        worldData[sLocation]?.let {
                            return it[key]
                        }
                    }
                }
            }
            return null
        }

        fun getDataOrFail(world: String, sLocation: String, key: String): String {
            if(data.containsKey(world)) {
                data[world]?.let { worldData ->
                    if(worldData.containsKey(sLocation)) {
                        worldData[sLocation]?.let {
                            return it[key] ?: throw IllegalArgumentException("The SLocation '${toString()}' doesn't have data of $key.")
                        }
                    }
                }
            }
            throw IllegalArgumentException("The SLocation '${toString()}' doesn't have data of $key.")
        }
        
        
        private fun checkWorldData(world: String) {
            if(!createdDataWorlds.contains(world)) {
                createdDataWorlds += world
                SLocationData(world)
            }
        }

        override fun init() {
            Bukkit.getServer().worlds.forEach { 
                checkWorldData(it.name)
            }
            
            subscribeEvent<WorldLoadEvent> { 
                checkWorldData(world.name)
            }
        }
    }
    
}