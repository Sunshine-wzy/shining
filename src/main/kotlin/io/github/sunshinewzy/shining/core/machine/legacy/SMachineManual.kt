package io.github.sunshinewzy.shining.core.machine.legacy

/**
 * 手动机器
 */
abstract class SMachineManual(
    id: String,
    name: String,
    wrench: SMachineWrench,
    structure: SMachineStructure
) : SMachine(id, name, wrench, structure) {

    final override fun runMachine(event: SMachineRunEvent) {
        if(event is SMachineRunEvent.Manual)
            manualRun(event, getLevel(event.sLoc))
    }
    
    abstract fun manualRun(event: SMachineRunEvent.Manual, level: Short)
    
}