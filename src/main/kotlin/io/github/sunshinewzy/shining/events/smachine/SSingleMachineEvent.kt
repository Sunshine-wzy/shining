package io.github.sunshinewzy.shining.events.smachine

import io.github.sunshinewzy.shining.core.machine.legacy.SSingleMachine
import org.bukkit.event.Event

abstract class SSingleMachineEvent(val sSingleMachine: SSingleMachine) : Event()