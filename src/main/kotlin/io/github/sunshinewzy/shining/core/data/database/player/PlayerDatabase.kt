package io.github.sunshinewzy.shining.core.data.database.player

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.ConcurrentHashMap

object PlayerDatabase {

    operator fun get(player: String): MutableMap<String, String> {
        return transaction {
            PlayerData.slice(PlayerData.key, PlayerData.value)
                .select {
                    PlayerData.player eq player
                }.associateTo(ConcurrentHashMap()) {
                    it[PlayerData.key] to it[PlayerData.value]
                }
        }
    }

    operator fun get(player: String, key: String): String? {
        return transaction { 
            PlayerData.slice(PlayerData.value)
                .select {
                    (PlayerData.player eq player) and (PlayerData.key eq key)
                }
                .limit(1)
                .firstNotNullOfOrNull { 
                    it[PlayerData.value]
                }
        }
    }

    operator fun set(player: String, key: String, data: String) {
        transaction { 
            if(get(player, key) == null) {
                PlayerData.insert { 
                    it[this.player] = player
                    it[this.key] = key
                    it[this.value] = data
                }
            } else {
                PlayerData.update({(PlayerData.player eq player) and (PlayerData.key eq key)}) { 
                    it[value] = data
                }
            }
        }
    }
    
    fun delete(player: String, key: String) {
        transaction { 
            PlayerData.deleteWhere { 
                (this.player eq player) and (this.key eq key)
            }
        }
    }
    
}