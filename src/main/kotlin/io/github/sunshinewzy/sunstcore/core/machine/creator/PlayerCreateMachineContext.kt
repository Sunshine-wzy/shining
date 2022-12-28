package io.github.sunshinewzy.sunstcore.core.machine.creator

import io.github.sunshinewzy.sunstcore.core.effect.EdgeCube
import io.github.sunshinewzy.sunstcore.core.effect.SParticle
import io.github.sunshinewzy.sunstcore.objects.SPosition
import org.bukkit.Particle
import org.bukkit.entity.Player
import taboolib.common.util.Location
import taboolib.module.effect.ParticleSpawner
import taboolib.platform.util.toBukkitLocation
import taboolib.platform.util.toProxyLocation

class PlayerCreateMachineContext(
    var status: Status = Status.SELECT_LEFT,
    var leftPosition: SPosition? = null,
    var rightPosition: SPosition? = null
) {
    
    

    fun checkSelect() {
        if(isFinishedSelect()) {
            
        }
        
    }
    
    fun isFinishedSelect(): Boolean =
        leftPosition != null && rightPosition != null
    
    fun playParticle(player: Player) {
        leftPosition?.let { pos ->
            SParticle.aroundBlock(player, Particle.VILLAGER_HAPPY, pos)
        }
        
        rightPosition?.let { pos ->
            SParticle.aroundBlock(player, Particle.VILLAGER_HAPPY, pos)
        }
        
        if(leftPosition != null && rightPosition != null) {
            val leftLoc = leftPosition?.toLocation() ?: return
            val rightLoc = rightPosition?.toLocation() ?: return

            EdgeCube(
                leftLoc.toProxyLocation(),
                rightLoc.toProxyLocation(),
                object : ParticleSpawner {
                    override fun spawn(location: Location) {
                        player.spawnParticle(Particle.VILLAGER_HAPPY, location.toBukkitLocation(), 1, 0.0, 0.0, 0.0)
                    }
                }
            ).show()
        }
    }
    
    
    
    enum class Status {
        SELECT_LEFT,
        SELECT_RIGHT,

    }
    
}