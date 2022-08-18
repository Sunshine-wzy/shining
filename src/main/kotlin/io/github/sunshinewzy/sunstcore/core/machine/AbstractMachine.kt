package io.github.sunshinewzy.sunstcore.core.machine

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.api.machine.IMachine
import kotlinx.serialization.Serializable

@Serializable
abstract class AbstractMachine(override val property: MachineProperty) : IMachine {

    open fun register() {
        SunSTCore.machineManager.register(this)
    }

}