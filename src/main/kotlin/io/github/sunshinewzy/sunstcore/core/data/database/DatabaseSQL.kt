package io.github.sunshinewzy.sunstcore.core.data.database

import taboolib.module.database.Host
import taboolib.module.database.SQL

class DatabaseSQL(host: Host<SQL>) : Database<SQL>(host) {
    override val type: DatabaseType = DatabaseType.SQL
}