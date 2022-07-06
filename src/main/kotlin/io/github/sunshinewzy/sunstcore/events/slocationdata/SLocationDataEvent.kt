package io.github.sunshinewzy.sunstcore.events.slocationdata

import io.github.sunshinewzy.sunstcore.objects.SLocation
import org.bukkit.event.Event

abstract class SLocationDataEvent(val sLocation: SLocation) : Event()