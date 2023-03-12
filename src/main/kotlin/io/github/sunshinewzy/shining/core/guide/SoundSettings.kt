package io.github.sunshinewzy.shining.core.guide

import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player

class SoundSettings @JvmOverloads constructor(var sound: Sound, var pitch: Float = 1f, var volume: Float = 1f) {

    @JvmOverloads
    fun playSound(player: Player, location: Location = player.location) {
        player.playSound(location, sound, volume, pitch)
    }

    fun playSound(world: World, location: Location) {
        world.playSound(location, sound, volume, pitch)
    }

}