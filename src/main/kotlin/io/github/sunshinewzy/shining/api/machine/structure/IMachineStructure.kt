package io.github.sunshinewzy.shining.api.machine.structure

import org.bukkit.Location

interface IMachineStructure {
    
    var strictMode: Boolean
    
    var ignoreAir: Boolean
    
    
    fun check(location: Location): Boolean
    
}