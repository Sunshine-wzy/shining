package io.github.sunshinewzy.shining.events.slocationdata

import io.github.sunshinewzy.shining.objects.SLocation
import org.bukkit.event.Event

abstract class SLocationDataEvent(val sLocation: SLocation) : Event()