package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.utils.ServerSoftware.*
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ServerUtils {

    val SERVER_SOFTWARE by lazy {
        when (Bukkit.getVersion().substringAfter('-').substringBefore('-')) {
            "Bukkit" -> CRAFT_BUKKIT
            "Spigot" -> SPIGOT
            "Paper" -> PAPER
            "Pufferfish" -> PUFFERFISH
            "Tuinity" -> TUINITY
            "Purpur" -> PURPUR
            "Airplane" -> AIRPLANE
            else -> UNKNOWN
        }
    }

}


enum class ServerSoftware(private vararg val superSoftwares: ServerSoftware = emptyArray()) {
    CRAFT_BUKKIT,
    SPIGOT(CRAFT_BUKKIT),
    PAPER(SPIGOT),
    PUFFERFISH(PAPER),
    TUINITY(PAPER),
    PURPUR(TUINITY, PUFFERFISH),
    AIRPLANE(PURPUR),
    UNKNOWN;

    val tree: List<ServerSoftware> = buildList {
        val unexplored = LinkedList<ServerSoftware>()
        unexplored += this@ServerSoftware

        generateSequence { unexplored.poll() }
            .forEach { software ->
                add(software)
                unexplored += software.superSoftwares
            }
    }

    fun <K, V> getCorrectMap(): MutableMap<K, V> = if (this == PURPUR) ConcurrentHashMap() else HashMap()

    fun isPaper() = this.tree.contains(PAPER)

}