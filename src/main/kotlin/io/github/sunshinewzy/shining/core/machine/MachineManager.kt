package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.machine.IMachine
import io.github.sunshinewzy.shining.api.machine.IMachineManager
import io.github.sunshinewzy.shining.api.machine.IMachineRegistrationProcessor
import io.github.sunshinewzy.shining.api.machine.component.MachineComponentLifecycle
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate3D
import io.github.sunshinewzy.shining.api.objects.position.Position3D
import io.github.sunshinewzy.shining.utils.position3D
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap

object MachineManager : IMachineManager {
    
    private val registeredProcessors = ConcurrentHashMap.newKeySet<IMachineRegistrationProcessor>()
    private val activeMachineMap: MutableMap<Position3D, IMachine> = ConcurrentHashMap()
    private val interactiveBlockToCenterMap: MutableMap<Position3D, Position3D> = ConcurrentHashMap()


    override fun activate(position: Position3D, machine: IMachine) {
        activeMachineMap[position] = machine
    }

    override fun deactivate(position: Position3D): IMachine? =
        activeMachineMap.remove(position)

    override fun run(position: Position3D): MachineRunContext? {
        activeMachineMap[position]?.let { machine ->
            return MachineRunContext(position, position, Coordinate3D.ORIGIN).also {
                machine.doLifecycle(MachineComponentLifecycle.RUN, it)
            }
        }

        interactiveBlockToCenterMap[position]?.let { center ->
            activeMachineMap[center]?.let { machine ->
                return MachineRunContext(center, position, Coordinate3D(position.x - center.x, position.y - center.y, position.z - center.z)).also {
                    machine.doLifecycle(MachineComponentLifecycle.RUN, it)
                }
            }
        }
        return null
    }

    override fun hasMachine(position: Position3D): Boolean =
        activeMachineMap.containsKey(position) || interactiveBlockToCenterMap.containsKey(position)

    override fun getMachine(position: Position3D): IMachine? =
        activeMachineMap[position]

    override fun getInteractiveBlockCenter(position: Position3D): Position3D? =
        interactiveBlockToCenterMap[position]

    override fun registerProcessor(processor: IMachineRegistrationProcessor) {
        registeredProcessors += processor
    }

    override fun unregisterProcessor(processor: IMachineRegistrationProcessor) {
        registeredProcessors -= processor
    }

    override fun isProcessorRegistered(processor: IMachineRegistrationProcessor): Boolean {
        return registeredProcessors.contains(processor)
    }
    
    
    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val clickedBlock = event.clickedBlock ?: return
        if (clickedBlock.type == Material.AIR || event.hand != EquipmentSlot.HAND) return

        val pos = clickedBlock.location.position3D
        activeMachineMap[pos]?.let { machine ->
            machine.doLifecycle(MachineComponentLifecycle.INTERACT, MachineInteractContext(pos, pos, Coordinate3D.ORIGIN, event))
            if (event.action == Action.RIGHT_CLICK_BLOCK) {
                machine.doLifecycle(MachineComponentLifecycle.RUN, MachineRunContext(pos, pos, Coordinate3D.ORIGIN))
            }
            return
        }

        interactiveBlockToCenterMap[pos]?.let { center ->
            activeMachineMap[center]?.let { machine ->
                machine.doLifecycle(MachineComponentLifecycle.INTERACT, MachineInteractContext(center, pos, Coordinate3D(pos.x - center.x, pos.y - center.y, pos.z - center.z), event))
                if (event.action == Action.RIGHT_CLICK_BLOCK) {
                    machine.doLifecycle(MachineComponentLifecycle.RUN, MachineRunContext(center, pos, Coordinate3D(pos.x - center.x, pos.y - center.y, pos.z - center.z)))
                }
                return
            }
        }
    }

}