package io.github.sunshinewzy.shining.core.machine.structure

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.api.universal.block.UniversalBlock
import io.github.sunshinewzy.shining.core.universal.block.VanillaUniversalBlock
import org.bukkit.Location
import org.bukkit.block.BlockFace

@JsonTypeName("single")
class SingleMachineStructure(var block: UniversalBlock) : AbstractMachineStructure() {

    constructor() : this(VanillaUniversalBlock())
    
    override fun check(location: Location, direction: BlockFace?): Boolean =
        block.compare(location.block, strictMode, ignoreAir)
    
}