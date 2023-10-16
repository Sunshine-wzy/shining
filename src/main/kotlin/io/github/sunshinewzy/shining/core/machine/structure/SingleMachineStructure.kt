package io.github.sunshinewzy.shining.core.machine.structure

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.api.universal.block.UniversalBlock
import io.github.sunshinewzy.shining.core.universal.block.VanillaUniversalBlock
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import java.util.function.BiConsumer

@JsonTypeName("single")
class SingleMachineStructure(var block: UniversalBlock) : AbstractMachineStructure() {

    constructor() : this(VanillaUniversalBlock())
    
    override fun check(location: Location, direction: BlockFace?): Boolean =
        block.compare(location.block, strictMode, ignoreAir)

    override fun build(location: Location, direction: BlockFace?) {
        block.setBlock(location.block)
    }

    override fun project(player: Player, location: Location, direction: BlockFace?) {
        block.sendBlock(player, location)
    }

    override fun forEachBlock(location: Location, direction: BlockFace?, action: BiConsumer<Location, UniversalBlock>) {
        action.accept(location, block)
    }
    
}