package io.github.sunshinewzy.sunstcore.events.smachine

import io.github.sunshinewzy.sunstcore.core.machine.legacy.SMachine
import org.bukkit.event.Event

abstract class SMachineEvent(val sMachine: SMachine) : Event()