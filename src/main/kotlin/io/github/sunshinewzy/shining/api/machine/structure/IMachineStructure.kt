package io.github.sunshinewzy.shining.api.machine.structure

import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.sunshinewzy.shining.api.universal.block.UniversalBlock
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import java.util.function.BiConsumer

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY
)
interface IMachineStructure {
    
    var strictMode: Boolean
    
    var ignoreAir: Boolean
    
    
    fun check(location: Location, direction: BlockFace?): Boolean
    
    fun build(location: Location, direction: BlockFace?)
    
    fun project(player: Player, location: Location, direction: BlockFace?)
    
    fun forEachBlock(location: Location, direction: BlockFace?, action: BiConsumer<Location, UniversalBlock>)
    
}