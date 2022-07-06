package io.github.sunshinewzy.sunstcore.objects.machine

import io.github.sunshinewzy.sunstcore.interfaces.Registrable

object SunSTMachineManager : Registrable {

    override fun register() {
        CraftingStation.register()
    }
    
}