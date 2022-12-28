package io.github.sunshinewzy.shining.events.smachine

import io.github.sunshinewzy.shining.core.machine.legacy.SMachine
import org.bukkit.event.Event

abstract class SMachineEvent(val sMachine: SMachine) : Event()