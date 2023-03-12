package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.machine.IMachine
import io.github.sunshinewzy.shining.api.machine.IMachineManager
import io.github.sunshinewzy.shining.api.machine.IMachineRegistrationProcessor
import io.github.sunshinewzy.shining.objects.SPosition
import io.github.sunshinewzy.shining.objects.SPosition.Companion.position
import org.bukkit.Location
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap

object MachineManager : IMachineManager {
    private val registeredProcessors = ConcurrentHashMap.newKeySet<IMachineRegistrationProcessor>()
    private val activeMachineMap: MutableMap<SPosition, IMachine> = ConcurrentHashMap()

    val SPosition.machine: IMachine?
        get() = activeMachineMap[this]
    val Location.machine: IMachine?
        get() = position.machine


    override fun activate(location: SPosition, machine: IMachine) {
        activeMachineMap[location] = machine
    }

    override fun deactivate(location: SPosition) {
        activeMachineMap -= location
    }

    override fun run(location: SPosition) {
        activeMachineMap[location]?.run()
    }

    override fun registerProcessor(processor: IMachineRegistrationProcessor) {
        registeredProcessors += processor
    }

    override fun unregisterProcessor(processor: IMachineRegistrationProcessor) {
        registeredProcessors -= processor
    }

    override fun isProcessorRegistered(processor: IMachineRegistrationProcessor): Boolean {
        return registeredProcessors.contains(processor)
    }


    @SubscribeEvent(EventPriority.HIGHEST)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return


    }

}