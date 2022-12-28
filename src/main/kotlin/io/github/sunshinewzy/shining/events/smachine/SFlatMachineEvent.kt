package io.github.sunshinewzy.shining.events.smachine

import io.github.sunshinewzy.shining.core.machine.legacy.SFlatMachine
import io.github.sunshinewzy.shining.objects.SLocation.Companion.toSLocation
import org.bukkit.Location
import org.bukkit.event.Event

abstract class SFlatMachineEvent(val sFlatMachine: SFlatMachine, val loc: Location) : Event() {
    val sLoc = loc.toSLocation()
}