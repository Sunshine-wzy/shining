package io.github.sunshinewzy.sunstcore.core.data.legacy.internal

import io.github.sunshinewzy.sunstcore.core.data.legacy.SConfig
import io.github.sunshinewzy.sunstcore.core.machine.legacy.SMachine

class SMachineConfig(val sMachine: SMachine) : SConfig(sMachine.wrench.plugin, sMachine.id, "SMachine") {
    
}