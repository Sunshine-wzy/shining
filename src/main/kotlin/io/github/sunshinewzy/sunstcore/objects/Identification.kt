package io.github.sunshinewzy.sunstcore.objects

import io.github.sunshinewzy.sunstcore.core.data.serializer.kotlinx.UUIDSerializer
import io.github.sunshinewzy.sunstcore.utils.findPlayer
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class Identification(
    @Serializable(UUIDSerializer::class)
    val uuid: UUID,
    val name: String
) {
    
    fun getPlayer(): Player? =
        uuid.findPlayer()
    
    
    companion object {
        private val cache = ConcurrentHashMap<UUID, Identification>()
        
        fun getIdentification(uuid: UUID, name: String): Identification {
            return cache[uuid] ?: Identification(uuid, name)
        }
        
        
        fun Player.getIdentification(): Identification {
            return getIdentification(uniqueId, name)
        }
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Identification) return false

        if(uuid != other.uuid) return false

        return true
    }
}