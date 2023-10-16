package io.github.sunshinewzy.shining.api.machine.structure

import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.util.Vector

interface IRotator : Cloneable {

    /**
     * Gets the rotation, in number of 90-degree clockwise rotations
     * @return The rotation
     */
    fun getRotation(): Int

    /**
     * Sets the rotation
     * @param rotation The rotation to set
     */
    fun setRotation(rotation: Int)

    /**
     * Gets whether this rotator mirrors over the X axis
     * @return Whether this rotator mirrors over the X axis
     */
    fun isMirrored(): Boolean

    /**
     * Sets whether this rotator mirrors over the X axis
     * @param mirrored Whether this rotator mirrors over the X axis
     */
    fun setMirrored(mirrored: Boolean)

    /**
     * @return The rotated relative X
     */
    fun getRotatedX(): Int

    /**
     * @return The rotated relative Z
     */
    fun getRotatedZ(): Int

    /**
     * Gets a Rotator which will negate the operations of this Rotator
     * @return The inverse Rotator
     */
    fun getInverse(): IRotator

    /**
     * Sets the relative coordinates this Rotator will rotate
     * @param x The relative X coordinate
     * @param z The relative Z coordinate
     */
    fun setLocation(x: Int, z: Int)

    /**
     * Rotates a Vector according to this Rotator
     * @param vector The Vector to rotate
     * @return The rotated Vector
     */
    fun rotateVector(vector: Vector): Vector

    /**
     * Rotates a BlockFace according to this Rotator
     * @param face The BlockFace to rotate
     * @return The rotated BlockFace
     */
    fun rotateBlockFace(face: BlockFace): BlockFace

    /**
     * Rotates block data. NOTE: Only works for 1.13+
     * @param data The block data to rotate
     * @return The rotated block data
     */
    fun rotateBlockData(data: BlockData): BlockData

    public override fun clone(): IRotator
    
}