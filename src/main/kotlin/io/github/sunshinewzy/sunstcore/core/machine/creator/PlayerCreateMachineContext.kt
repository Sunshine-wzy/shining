package io.github.sunshinewzy.sunstcore.core.machine.creator

import io.github.sunshinewzy.sunstcore.objects.SPosition
import io.github.sunshinewzy.sunstcore.utils.SParticle
import org.bukkit.Particle
import org.bukkit.entity.Player

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
        
        
    }
    

    enum class Status {
        SELECT_LEFT,
        SELECT_RIGHT,

    }
    
}