package io.github.sunshinewzy.sunstcore.events.smachine

import io.github.sunshinewzy.sunstcore.core.machine.legacy.SSingleMachine
import org.bukkit.event.Event

abstract class SSingleMachineEvent(val sSingleMachine: SSingleMachine) : Event()