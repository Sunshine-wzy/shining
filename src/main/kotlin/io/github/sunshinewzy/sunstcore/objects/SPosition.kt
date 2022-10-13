package io.github.sunshinewzy.sunstcore.objects

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions

data class SPosition @JvmOverloads constructor(val x: Int, val y: Int, val z: Int, val world: String? = null) {

    @JvmOverloads
    constructor(y: Int, world: String? = null) : this(0, y, 0, world)
    
    override fun toString(): String = "$x,$y,$z;$world"
    
    fun toLocation(): Location? {
        if(world == null) return null
        return Location(Bukkit.getWorld(world), OFFSET + x, OFFSET + y, OFFSET + z)
    }
    
    fun spawnParticle(player: Player, particle: Particle, count: Int, offsetX: Double, offsetY: Double, offsetZ: Double) {
        player.spawnParticle(Particle.VILLAGER_HAPPY, x.toDouble(), y.toDouble(), z.toDouble(), count, offsetX, offsetY, offsetZ)
    }
    
    
    companion object {
        const val OFFSET: Double = 0.5
    
        @JvmStatic
        fun fromString(source: String): SPosition? {
            val posAndWorld = source.split(';')
            if(posAndWorld.size != 2) return null
            
            val pos = posAndWorld[0].split(",")
            if(pos.size != 3) return null
            val (x, y, z) = pos.map { 
                NumberConversions.toInt(it)
            }
            
            val world = posAndWorld[1]
            if(world.isEmpty())
                return SPosition(x, y, z)
            
            return SPosition(x, y, z, world)
        }
        
        
        val Location.position: SPosition
            get() = SPosition(blockX, blockY, blockZ)
        
    }
    
}