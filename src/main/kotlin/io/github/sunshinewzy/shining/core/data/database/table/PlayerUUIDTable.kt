package io.github.sunshinewzy.shining.core.data.database.table

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import java.util.*

open class PlayerUUIDTable(name: String = "", columnName: String = "uuid") : IdTable<UUID>(name) {
    final override val id: Column<EntityID<UUID>> = uuid(columnName).entityId()
    final override val primaryKey: PrimaryKey = PrimaryKey(id)
}