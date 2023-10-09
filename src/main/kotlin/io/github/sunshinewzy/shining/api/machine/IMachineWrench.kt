package io.github.sunshinewzy.shining.api.machine

import org.bukkit.Location
import org.bukkit.entity.Player

interface IMachineWrench {
    
    fun registerMachine(machine: IMachine)
    
    fun check(location: Location, player: Player?)
    
}