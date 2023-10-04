package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.api.objects.position.Position3D
import org.bukkit.Location

val Location.position3D: Position3D
    get() = Position3D(blockX, blockY, blockZ, world?.toString())