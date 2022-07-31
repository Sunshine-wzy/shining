package io.github.sunshinewzy.sunstcore.modules.data.internal

import io.github.sunshinewzy.sunstcore.modules.data.SAutoCoverSaveData
import io.github.sunshinewzy.sunstcore.modules.machine.SFlatMachine
import io.github.sunshinewzy.sunstcore.modules.machine.SFlatMachineInformation
import io.github.sunshinewzy.sunstcore.objects.SLocation
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

class SFlatMachineData(
    val plugin: JavaPlugin,
    val sFlatMachine: SFlatMachine
) : SAutoCoverSaveData(plugin, sFlatMachine.id, "SFlatMachine") {

    override fun YamlConfiguration.modifyConfig() {
        sFlatMachine.flatMachines.forEach { (sLoc, information) -> 
            set(sLoc.toString(), information)
        }
    }

    override fun YamlConfiguration.loadConfig() {
        val roots = getKeys(false)
        roots.forEach { sLoc ->
            val information = get(sLoc) as? SFlatMachineInformation ?: SFlatMachineInformation()
            SFlatMachine.addMachine(SLocation(sLoc), sFlatMachine, information)
        }
    }
    
    
}