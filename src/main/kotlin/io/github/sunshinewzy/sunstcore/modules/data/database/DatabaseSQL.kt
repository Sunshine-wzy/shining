package io.github.sunshinewzy.sunstcore.modules.data.database

import taboolib.module.database.Host
import taboolib.module.database.SQL

class DatabaseSQL(host: Host<SQL>) : SDatabase<SQL>(host) {
    override val type: DatabaseType = DatabaseType.SQL
}