package io.github.sunshinewzy.shining.objects

import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material

object SCollection {
    
    val colors = arrayListOf(Color.AQUA, Color.BLACK, Color.BLUE, Color.FUCHSIA, Color.GRAY, Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL, Color.WHITE, Color.YELLOW)
    val fireworkEffectTypes = FireworkEffect.Type.values()
    val materials = Material.values()

    
    fun matchMaterials(name: String, isEndsWith: Boolean = true): List<Material> {
        val list = arrayListOf<Material>()

        if(isEndsWith) {
            materials.forEach {
                if(it.name.endsWith(name, true)) {
                    list += it
                }
            }
        } else {
            materials.forEach {
                if(it.name.contains(name, true)) {
                    list += it
                }
            }
        }

        return list
    }
    
    
}