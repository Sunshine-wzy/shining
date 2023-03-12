package io.github.sunshinewzy.shining.objects.machine

import io.github.sunshinewzy.shining.interfaces.Registrable

object SunSTMachineManager : Registrable {

    override fun register() {
        CraftingStation.register()
    }

}