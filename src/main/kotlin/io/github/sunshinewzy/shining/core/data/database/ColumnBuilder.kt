package io.github.sunshinewzy.shining.core.data.database

import taboolib.module.database.Column
import taboolib.module.database.SQL
import taboolib.module.database.SQLite

class ColumnBuilder(val name: String? = null) {
    private val sql = SQL()
    private val sqlite = SQLite()
    
    init {
        name?.let { 
            sql.name(it)
            sqlite.name(it)
        }
    }
    
    
    fun sql(func: SQL.() -> Unit) {
        func(sql)
    }
    
    fun sqlite(func: SQLite.() -> Unit) {
        func(sqlite)
    }
    
    fun getColumn(type: DatabaseType): Column =
        when(type) {
            DatabaseType.SQL -> sql.getColumn()
            DatabaseType.SQLite -> sqlite.getColumn()
        }
    
}