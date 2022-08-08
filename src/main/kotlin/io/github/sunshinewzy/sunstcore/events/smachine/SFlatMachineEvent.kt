package io.github.sunshinewzy.sunstcore.events.smachine

import io.github.sunshinewzy.sunstcore.core.machine.legacy.SFlatMachine
import io.github.sunshinewzy.sunstcore.objects.SLocation.Companion.toSLocation
import org.bukkit.Location
import org.bukkit.event.Event

abstract class SFlatMachineEvent(val sFlatMachine: SFlatMachine, val loc: Location) : Event() {
    val sLoc = loc.toSLocation()
}