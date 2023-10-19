package io.github.sunshinewzy.shining.core.machine.creator

import io.github.sunshinewzy.shining.api.objects.position.Position3D
import io.github.sunshinewzy.shining.core.effect.EdgeCube
import io.github.sunshinewzy.shining.core.effect.ShiningParticle
import org.bukkit.Particle
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import taboolib.common.util.Location
import taboolib.module.effect.ParticleSpawner
import taboolib.platform.util.toBukkitLocation
import taboolib.platform.util.toProxyLocation

data class MachineCreatorContext(
    var center: Position3D? = null,
    var direction: BlockFace? = null,
    var first: Position3D? = null,
    var second: Position3D? = null
) {

    fun isFinishedSelect(): Boolean =
        center != null && direction != null && first != null && second != null

    fun playParticle(player: Player) {
        center?.let { pos ->
            ShiningParticle.aroundBlock(player, Particle.REDSTONE, pos)
        }
        
        first?.let { pos ->
            ShiningParticle.aroundBlock(player, Particle.LAVA, pos)
        }

        second?.let { pos ->
            ShiningParticle.aroundBlock(player, Particle.LAVA, pos)
        }

        if (first != null && second != null) {
            val leftLoc = first?.toLocationOrNull() ?: return
            val rightLoc = second?.toLocationOrNull() ?: return

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

}