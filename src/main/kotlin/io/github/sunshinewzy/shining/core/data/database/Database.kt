package io.github.sunshinewzy.shining.core.data.database

import taboolib.module.database.ColumnBuilder
import taboolib.module.database.Host
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

abstract class Database<T: ColumnBuilder>(private val host: Host<T>) {
    private val tableMap = ConcurrentHashMap<String, Table<Host<T>, T>>()
    
    val dataSource: DataSource = host.createDataSource()
    
    
    abstract val type: DatabaseType
    
    
    operator fun get(tableName: String): Table<Host<T>, T>? {
        return tableMap[tableName]
    }
    
    fun get(tableName: String, func: Table<Host<T>, T>.() -> Unit = {}): Table<Host<T>, T> {
        return Table(tableName, host, type, func).also {
            it.createTable(dataSource)
            tableMap[tableName] = it
        }
    }
    
    
    fun table(name: String, func: Table<Host<T>, T>.() -> Unit = {}): Database<T> {
        tableMap[name] = Table(name, host, type, func).also { it.createTable(dataSource) }
        return this
    }
    
}