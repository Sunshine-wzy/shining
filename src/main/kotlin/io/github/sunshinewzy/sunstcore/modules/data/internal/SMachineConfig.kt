package io.github.sunshinewzy.sunstcore.modules.data.internal

import io.github.sunshinewzy.sunstcore.modules.data.SConfig
import io.github.sunshinewzy.sunstcore.modules.machine.SMachine

class SMachineConfig(val sMachine: SMachine) : SConfig(sMachine.wrench.plugin, sMachine.id, "SMachine") {
    
}