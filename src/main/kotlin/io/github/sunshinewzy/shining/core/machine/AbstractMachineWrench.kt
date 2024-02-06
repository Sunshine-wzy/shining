package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.machine.IMachine
import io.github.sunshinewzy.shining.api.machine.IMachineWrench
import io.github.sunshinewzy.shining.core.lang.sendPrefixedLangText
import io.github.sunshinewzy.shining.utils.position3D
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import java.util.*

abstract class AbstractMachineWrench : IMachineWrench {

    protected val machineRegistry: MutableMap<Material, MutableList<IMachine>> = EnumMap(Material::class.java)

    
    override fun registerMachine(machine: IMachine) {
        machineRegistry
            .getOrPut(machine.structure.getCenterBlock().getType()) { ArrayList() }
            .add(machine)
    }

    override fun check(location: Location, direction: BlockFace?, player: Player?): Boolean {
        val position = location.position3D
        if (MachineManager.hasMachine(position)) {
            player?.sendPrefixedLangText("text-machine-wrench-build-failure-already_exists")
            return false
        }

        machineRegistry[location.block.type]?.let { list ->
            for (machine in list) {
                if (machine.structure.check(location, direction)) {
                    MachineManager.activate(position, machine)

                    player?.sendPrefixedLangText("text-machine-wrench-build-success")
                    player?.playEffect(location, Effect.ENDER_SIGNAL, 1)
                    player?.playEffect(location, Effect.CLICK1, 1)
                    return true
                }
            }
        }
        return false
    }
    
}