package io.github.sunshinewzy.shining.core.data.legacy.internal

import io.github.sunshinewzy.shining.core.data.legacy.SConfig
import io.github.sunshinewzy.shining.core.machine.legacy.SMachine

class SMachineConfig(val sMachine: SMachine) : SConfig(sMachine.wrench.plugin, sMachine.id, "SMachine") {
    
}