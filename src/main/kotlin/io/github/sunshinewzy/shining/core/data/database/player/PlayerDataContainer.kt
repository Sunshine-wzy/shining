package io.github.sunshinewzy.shining.core.data.database.player

import taboolib.common.platform.Schedule
import taboolib.common.platform.function.submitAsync
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class PlayerDataContainer(val player: String) {

    val source: MutableMap<String, String> = PlayerDatabase[player]
    val updateMap: MutableMap<String, Long> = ConcurrentHashMap()


    operator fun set(key: String, value: Any) {
        source[key] = value.toString()
        save(key)
    }

    fun setDelayed(key: String, value: Any, delay: Long = 3L, timeUnit: TimeUnit = TimeUnit.SECONDS) {
        source[key] = value.toString()
        updateMap[key] = System.currentTimeMillis() - timeUnit.toMillis(delay)
    }

    operator fun get(key: String): String? {
        return source[key]
    }

    fun delete(key: String) {
        source.remove(key)
        submitAsync { PlayerDatabase.delete(player, key) }
    }

    fun keys(): Set<String> {
        return source.keys
    }

    fun values(): Map<String, String> {
        return source
    }

    fun size(): Int {
        return source.size
    }

    fun save(key: String) {
        submitAsync { PlayerDatabase[player, key] = source[key]!! }
    }

    fun checkUpdate() {
        updateMap.filterValues { it < System.currentTimeMillis() }.forEach { (t, _) ->
            updateMap.remove(t)
            save(t)
        }
    }

    override fun toString(): String {
        return "DataContainer(user='$player', source=$source)"
    }

    companion object {

        @Schedule(period = 20)
        private fun checkUpdate() {
            PlayerDatabaseHandler.dataContainer.values.forEach { it.checkUpdate() }
        }

    }
}