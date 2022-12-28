package io.github.sunshinewzy.shining.core.machine.legacy

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * 自动机器
 */
abstract class SMachineTimer(
    id: String,
    name: String,
    wrench: SMachineWrench,
    structure: SMachineStructure,
    val plugin: JavaPlugin,
    val period: Long,
) : SMachine(id, name, wrench, structure) {
    
    init {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            
        }, period, period)
        
    }
    
    final override fun runMachine(event: SMachineRunEvent) {
        if(event is SMachineRunEvent.Timer)
            timerRun(event, getLevel(event.sLoc))
    }
    
    abstract fun timerRun(event: SMachineRunEvent.Timer, level: Short)
}