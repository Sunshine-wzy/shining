package io.github.sunshinewzy.sunstcore.utils

import io.github.sunshinewzy.sunstcore.objects.SPosition
import org.bukkit.Particle
import org.bukkit.entity.Player

object SParticle {
    
    fun aroundBlock(player: Player, particle: Particle, position: SPosition, count: Int = 1, step: Double = 0.5) {
        var i = 0.0
        while(i <= 1.0) {
            position.spawnParticle(player, particle, count, i, 0.0, 0.0)
            position.spawnParticle(player, particle, count, 0.0, i, 0.0)
            position.spawnParticle(player, particle, count, 0.0, 0.0, i)
            position.spawnParticle(player, particle, count, i, 1.0, 0.0)
            position.spawnParticle(player, particle, count, 0.0, 1.0, i)
            position.spawnParticle(player, particle, count, i, 0.0, 1.0)
            position.spawnParticle(player, particle, count, 0.0, i, 1.0)
            position.spawnParticle(player, particle, count, 1.0, i, 0.0)
            position.spawnParticle(player, particle, count, 1.0, 0.0, i)
            position.spawnParticle(player, particle, count, 1.0, 1.0, i)
            position.spawnParticle(player, particle, count, 1.0, i, 1.0)
            position.spawnParticle(player, particle, count, i, 1.0, 1.0)
            
            i += step
        }
        
    }
    
}