package io.github.sunshinewzy.shining.core.effect

import io.github.sunshinewzy.shining.api.objects.position.Position3D
import org.bukkit.Particle
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import taboolib.module.effect.ParticleObj
import java.util.*

object ShiningParticle {
    const val PERIOD = 20L

    private val particles: MutableList<ParticleObj> = LinkedList()


    @Awake(LifeCycle.ENABLE)
    fun scheduler() {
        submit(delay = PERIOD, period = PERIOD) {
            particles.forEach {

            }
        }
    }

    fun addTask(task: ParticleObj) {
        task.turnOffTask()

        submit(delay = 2) {

        }
        particles += task
    }


    fun aroundBlock(player: Player, particle: Particle, position: Position3D, count: Int = 1, step: Double = 0.5) {
        var i = 0.0
        while (i <= 1.0) {
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