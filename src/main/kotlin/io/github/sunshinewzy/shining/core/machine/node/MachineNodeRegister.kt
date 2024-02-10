package io.github.sunshinewzy.shining.core.machine.node

import io.github.sunshinewzy.shining.api.blueprint.BlueprintNodeType
import io.github.sunshinewzy.shining.core.blueprint.BlueprintNodeRegistry.register
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object MachineNodeRegister {
    
    @Awake(LifeCycle.ACTIVE)
    fun onActive() {
        register(BlueprintNodeType.EVENT, MachineInteractEventNode())
        register(BlueprintNodeType.EVENT, MachineRunEventNode())
    }
    
}