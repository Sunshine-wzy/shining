package io.github.sunshinewzy.shining.api.machine.structure

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.bukkit.Location
import org.bukkit.block.BlockFace

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY
)
interface IMachineStructure {
    
    var strictMode: Boolean
    
    var ignoreAir: Boolean
    
    
    fun check(location: Location, direction: BlockFace?): Boolean
    
}