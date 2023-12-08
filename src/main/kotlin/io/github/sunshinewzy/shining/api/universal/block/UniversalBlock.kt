package io.github.sunshinewzy.shining.api.universal.block

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.sunshinewzy.shining.api.machine.structure.IRotator
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.entity.Player

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY
)
interface UniversalBlock {

    /**
     * Sets this [UniversalBlock] at the given location of [block].
     * 
     * @param block The block to set
     */
    @JsonIgnore
    fun setBlock(block: Block)
    
    /**
     * Gets the BlockState to set for the given [block].
     * 
     * @param block The Block to get the BlockState at
     * @return The BlockState that would be set
     */
    @JsonIgnore
    fun getState(block: Block): BlockState

    /**
     * Sends a fake block change to the [player].
     * 
     * @param player The Player to send the fake block change to
     * @param location The Location of the fake block
     */
    fun sendBlock(player: Player, location: Location)

    /**
     * @return Whether the Material is air - for 1.15+, AIR, CAVE_AIR, or VOID_AIR
     */
    @JsonIgnore
    fun isAir(): Boolean

    /**
     * @return The type of this [UniversalBlock]
     */
    @JsonIgnore
    fun getType(): Material

    /**
     * Compares this [UniversalBlock] to the [block].
     * 
     * @param block The Block to compare with
     * @param strict Whether to compare strictly
     * @param ignoreAir Whether to return true automatically if this [UniversalBlock] is air
     * @return Whether the block matches this [UniversalBlock] within the given parameters
     */
    fun compare(block: Block, strict: Boolean, ignoreAir: Boolean): Boolean
    
    fun rotate(rotator: IRotator): UniversalBlock
    
}