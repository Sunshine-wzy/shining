package io.github.sunshinewzy.sunstcore.modules.data.sunst

import io.github.sunshinewzy.sunstcore.modules.data.SAutoCoverSaveData
import io.github.sunshinewzy.sunstcore.modules.machine.SMachine
import io.github.sunshinewzy.sunstcore.modules.machine.SMachineInformation
import io.github.sunshinewzy.sunstcore.objects.SLocation
import io.github.sunshinewzy.sunstcore.utils.castMap
import org.bukkit.configuration.file.YamlConfiguration

class SMachineData(val sMachine: SMachine) : SAutoCoverSaveData(sMachine.wrench.plugin, sMachine.id, "SMachine") {

    override fun YamlConfiguration.modifyConfig() {
        sMachine.sMachines.forEach { (sLoc, information) ->
            set(sLoc.toString(), information)
        }
        
        set("Config.Recipes", sMachine.recipes)
    }

    override fun YamlConfiguration.loadConfig() {
        val roots = getKeys(false)
        roots.forEach { sLoc ->
            if(sLoc == "Config") return@forEach
            
            val information = get(sLoc) as? SMachineInformation ?: SMachineInformation()
            sMachine.addMachine(SLocation(sLoc), information)
        }

        get("Config.Recipes")?.castMap(sMachine.recipes)
    }
    
}