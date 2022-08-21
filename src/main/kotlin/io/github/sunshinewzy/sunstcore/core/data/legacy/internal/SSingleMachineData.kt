package io.github.sunshinewzy.sunstcore.core.data.legacy.internal

import io.github.sunshinewzy.sunstcore.core.data.legacy.SAutoCoverSaveData
import io.github.sunshinewzy.sunstcore.core.machine.legacy.SSingleMachine
import io.github.sunshinewzy.sunstcore.core.machine.legacy.SSingleMachineInformation
import io.github.sunshinewzy.sunstcore.objects.SLocation
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

class SSingleMachineData(
    val plugin: JavaPlugin,
    val sSingleMachine: SSingleMachine
) : SAutoCoverSaveData(plugin, sSingleMachine.id, "SSingleMachine") {

    override fun YamlConfiguration.modifyConfig() {
        sSingleMachine.singleMachines.forEach { (sLoc, information) -> 
            set(sLoc.toString(), information)
        }
    }

    override fun YamlConfiguration.loadConfig() {
        val roots = getKeys(false)
        roots.forEach { sLoc ->
            val information = get(sLoc) as? SSingleMachineInformation ?: SSingleMachineInformation()
            SSingleMachine.addMachine(SLocation(sLoc), sSingleMachine, information)
        }
    }
    
    
}