package io.github.sunshinewzy.sunstcore.modules.data.database

import taboolib.module.database.ColumnBuilder
import taboolib.module.database.Host
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

abstract class SDatabase<T: ColumnBuilder>(private val host: Host<T>) {
    private val tableMap = ConcurrentHashMap<String, STable<Host<T>, T>>()
    
    val dataSource: DataSource = host.createDataSource()
    
    
    abstract val type: DatabaseType
    
    
    operator fun get(tableName: String): STable<Host<T>, T>? {
        return tableMap[tableName]
    }
    
    fun get(tableName: String, func: STable<Host<T>, T>.() -> Unit = {}): STable<Host<T>, T> {
        return STable(tableName, host, type, func).also {
            it.createTable(dataSource)
            tableMap[tableName] = it
        }
    }
    
    
    fun table(name: String, func: STable<Host<T>, T>.() -> Unit = {}): SDatabase<T> {
        tableMap[name] = STable(name, host, type, func).also { it.createTable(dataSource) }
        return this
    }
    
}