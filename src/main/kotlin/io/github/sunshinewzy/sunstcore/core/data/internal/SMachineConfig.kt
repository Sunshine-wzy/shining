package io.github.sunshinewzy.sunstcore.core.data.internal

import io.github.sunshinewzy.sunstcore.core.data.SConfig
import io.github.sunshinewzy.sunstcore.core.machine.legacy.SMachine

class SMachineConfig(val sMachine: SMachine) : SConfig(sMachine.wrench.plugin, sMachine.id, "SMachine") {
    
}