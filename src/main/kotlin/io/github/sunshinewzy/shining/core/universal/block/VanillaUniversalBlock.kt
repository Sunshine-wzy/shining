package io.github.sunshinewzy.shining.core.universal.block

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.api.machine.structure.IRotator
import io.github.sunshinewzy.shining.api.universal.block.UniversalBlock
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import taboolib.module.nms.MinecraftVersion

@JsonTypeName("vanilla")
class VanillaUniversalBlock(val data: BlockData) : UniversalBlock {
    
    constructor() : this(Bukkit.createBlockData(Material.AIR))
    
    constructor(string: String) : this(Bukkit.createBlockData(string))
    
    constructor(type: Material) : this(type.createBlockData())


    override fun setBlock(block: Block) {
        block.setBlockData(data, false)
    }

    override fun getState(block: Block): BlockState {
        val state = block.state
        state.blockData = data
        return state
    }

    override fun sendBlock(player: Player, location: Location) {
        player.sendBlockChange(location, data)
    }

    override fun isAir(): Boolean =
        if (MinecraftVersion.major >= MinecraftVersion.V1_15) getType().isAir
        else getType() == Material.AIR
    
    override fun getType(): Material = data.material

    override fun compare(block: Block, strict: Boolean, ignoreAir: Boolean): Boolean {
        if (ignoreAir && isAir()) return true
        if (!strict) return block.type == getType()
        return block.blockData.matches(data)
    }

    override fun rotate(rotator: IRotator): VanillaUniversalBlock =
        VanillaUniversalBlock(rotator.rotateBlockData(data))
    
}