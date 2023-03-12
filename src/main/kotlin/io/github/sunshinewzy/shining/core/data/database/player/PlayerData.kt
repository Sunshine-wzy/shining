package io.github.sunshinewzy.shining.core.data.database.player

import org.jetbrains.exposed.sql.Table

object PlayerData : Table() {

    val player = varchar("player", 36).index()
    val key = varchar("key", 64).index()
    val value = text("value")

}