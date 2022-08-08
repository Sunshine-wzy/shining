package io.github.sunshinewzy.sunstcore.core.data.database

import taboolib.common.io.newFile
import taboolib.module.database.SQLite
import taboolib.module.database.getHost
import java.io.File

class DatabaseSQLite(file: File) : Database<SQLite>(newFile(file).getHost()) {
    override val type: DatabaseType = DatabaseType.SQLite
}