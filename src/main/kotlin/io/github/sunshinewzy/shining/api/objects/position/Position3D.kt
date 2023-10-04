package io.github.sunshinewzy.shining.api.objects.position

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player

data class Position3D @JvmOverloads constructor(val x: Int, val y: Int, val z: Int, val world: String? = null) {

    @JvmOverloads
    constructor(y: Int, world: String? = null) : this(0, y, 0, world)

    override fun toString(): String = "$x,$y,$z;$world"

    fun toLocation(): Location? {
        if (world == null) return null
        return Location(Bukkit.getWorld(world), OFFSET + x, OFFSET + y, OFFSET + z)
    }

    fun spawnParticle(
        player: Player,
        particle: Particle,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double
    ) {
        player.spawnParticle(particle, x.toDouble(), y.toDouble(), z.toDouble(), count, offsetX, offsetY, offsetZ)
    }

    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (!javaClass.equals(other.javaClass)) return false
        other as Position3D
        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (world == null && other.world == null) return true
        if (world != null && other.world != null) return world.equals(other.world)
        return false
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        result = 31 * result + (world?.hashCode() ?: 0)
        return result
    }


    companion object {
        const val OFFSET: Double = 0.5

        @JvmStatic
        fun fromString(source: String): Position3D? =
            PositionUtils.position3DFromString(source)
    }

}