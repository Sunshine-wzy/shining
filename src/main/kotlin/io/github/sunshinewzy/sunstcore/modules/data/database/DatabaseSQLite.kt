package io.github.sunshinewzy.sunstcore.modules.data.database

import taboolib.common.io.newFile
import taboolib.module.database.SQLite
import taboolib.module.database.getHost
import java.io.File

class DatabaseSQLite(file: File) : SDatabase<SQLite>(newFile(file).getHost()) {
    override val type: DatabaseType = DatabaseType.SQLite
}