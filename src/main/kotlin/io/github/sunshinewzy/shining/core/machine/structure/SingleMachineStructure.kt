package io.github.sunshinewzy.shining.core.machine.structure

import io.github.sunshinewzy.shining.api.universal.block.UniversalBlock
import org.bukkit.Location

class SingleMachineStructure(val block: UniversalBlock) : AbstractMachineStructure() {

    override fun check(location: Location): Boolean =
        block.compare(location.block, strictMode, ignoreAir)
    
}