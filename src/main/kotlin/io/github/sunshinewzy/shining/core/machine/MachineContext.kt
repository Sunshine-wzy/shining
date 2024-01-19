package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.machine.IMachineContext
import io.github.sunshinewzy.shining.api.machine.IMachineInteractContext
import io.github.sunshinewzy.shining.api.machine.IMachineRunContext
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate3D
import io.github.sunshinewzy.shining.api.objects.position.Position3D
import org.bukkit.event.player.PlayerInteractEvent

open class MachineContext(override val position: Position3D) : IMachineContext

open class MachineRunContext(
    position: Position3D,
    override val runPosition: Position3D,
    override val runCoordinate: Coordinate3D
) : MachineContext(position), IMachineRunContext

open class MachineInteractContext(
    position: Position3D,
    runPosition: Position3D,
    runCoordinate: Coordinate3D,
    override val interactEvent: PlayerInteractEvent
) : MachineRunContext(position, runPosition, runCoordinate), IMachineInteractContext
