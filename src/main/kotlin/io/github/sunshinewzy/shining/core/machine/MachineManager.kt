package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.machine.IMachine
import io.github.sunshinewzy.shining.api.machine.IMachineManager
import io.github.sunshinewzy.shining.api.machine.IMachineRegistrationProcessor
import io.github.sunshinewzy.shining.api.machine.event.run.MachineInteractEvent
import io.github.sunshinewzy.shining.api.machine.event.run.MachineRunEvent
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
    
    private val registeredProcessors: MutableSet<IMachineRegistrationProcessor> = ConcurrentHashMap.newKeySet()
    private val activeMachineMap: MutableMap<Position3D, IMachine> = ConcurrentHashMap()
    private val activeMachineToCenters: MutableMap<IMachine, MutableSet<Position3D>> = ConcurrentHashMap()
    private val interactiveBlockToCenter: MutableMap<Position3D, Position3D> = ConcurrentHashMap()
    private val coordinateEventPositionToCenters: MutableMap<Position3D, MutableSet<Position3D>> = ConcurrentHashMap()
    private val coordinateEventCenterToPositions: MutableMap<Position3D, MutableSet<Position3D>> = ConcurrentHashMap()
    

    override fun activate(position: Position3D, machine: IMachine) {
        activeMachineMap[position] = machine
        activeMachineToCenters.getOrPut(machine) { ConcurrentHashMap.newKeySet() }.add(position)
    }

    override fun deactivate(position: Position3D): IMachine? =
        activeMachineMap.remove(position)?.also { 
            activeMachineToCenters[it]?.remove(position)
        }

    override fun run(position: Position3D): MachineRunEvent? {
        activeMachineMap[position]?.let { machine ->
            return MachineRunEvent(position, position, Coordinate3D.ORIGIN).also {
                machine.callEvent(it)
            }
        }

        interactiveBlockToCenter[position]?.let { center ->
            activeMachineMap[center]?.let { machine ->
                return MachineRunEvent(center, position, Coordinate3D(position.x - center.x, position.y - center.y, position.z - center.z)).also {
                    machine.callEvent(it)
                }
            }
        }
        return null
    }

    override fun hasMachine(position: Position3D): Boolean =
        activeMachineMap.containsKey(position) || interactiveBlockToCenter.containsKey(position)

    override fun getMachine(position: Position3D): IMachine? =
        activeMachineMap[position]

    override fun bindCoordinateEventPosition(machine: IMachine) {
        activeMachineToCenters[machine]?.forEach { center ->
            machine.getCoordinateEventCoordinates().forEach { coordinate ->
                val pos = center + coordinate
                coordinateEventPositionToCenters
                    .getOrPut(pos) { ConcurrentHashMap.newKeySet() }
                    .add(center)
                coordinateEventCenterToPositions
                    .getOrPut(center) { ConcurrentHashMap.newKeySet() }
                    .add(pos)
            }
        }
    }

    override fun bindCoordinateEventPosition(machine: IMachine, coordinate: Coordinate3D) {
        activeMachineToCenters[machine]?.forEach { center ->
            val pos = center + coordinate
            coordinateEventPositionToCenters
                .getOrPut(pos) { ConcurrentHashMap.newKeySet() }
                .add(center)
            coordinateEventCenterToPositions
                .getOrPut(center) { ConcurrentHashMap.newKeySet() }
                .add(pos)
        }
    }

    override fun unbindCoordinateEventPosition(machine: IMachine) {
        activeMachineToCenters[machine]?.forEach { center ->
            coordinateEventCenterToPositions.remove(center)?.forEach { pos ->
                coordinateEventPositionToCenters[pos]?.remove(center)
            }
        }
    }

    override fun unbindCoordinateEventPosition(machine: IMachine, coordinate: Coordinate3D) {
        activeMachineToCenters[machine]?.forEach { center ->
            val pos = center + coordinate
            coordinateEventCenterToPositions[center]?.remove(pos)
            coordinateEventPositionToCenters[pos]?.remove(center)
        }
    }

    override fun getInteractiveBlockCenter(position: Position3D): Position3D? =
        interactiveBlockToCenter[position]

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
            val interact = machine.callEvent(MachineInteractEvent(pos, pos, Coordinate3D.ORIGIN, event))
            if (!interact) event.isCancelled = true
            if (event.action == Action.RIGHT_CLICK_BLOCK) {
                machine.callEvent(MachineRunEvent(pos, pos, Coordinate3D.ORIGIN).also { it.isCancelled = interact })
            }
            return
        }

        interactiveBlockToCenter[pos]?.let { center ->
            activeMachineMap[center]?.let { machine ->
                val interact = machine.callEvent(MachineInteractEvent(center, pos, Coordinate3D(pos.x - center.x, pos.y - center.y, pos.z - center.z), event))
                if (!interact) event.isCancelled = true
                if (event.action == Action.RIGHT_CLICK_BLOCK) {
                    machine.callEvent(MachineRunEvent(center, pos, Coordinate3D(pos.x - center.x, pos.y - center.y, pos.z - center.z)).also { it.isCancelled = interact })
                }
                return
            }
        }
    }

}