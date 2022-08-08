package io.github.sunshinewzy.sunstcore.core.data.database

import taboolib.module.database.ColumnBuilder
import taboolib.module.database.Host
import taboolib.module.database.Table

@Suppress("LeakingThis")
open class Table<T: Host<E>, E: ColumnBuilder>(
    name: String,
    host: Host<E>,
    val type: DatabaseType,
    func: io.github.sunshinewzy.sunstcore.core.data.database.Table<T, E>.() -> Unit = {}
) : Table<T, E>(name, host) {
    
    init {
        func(this)
    }
    
    
    open fun build(name: String? = null, tableBuilder: io.github.sunshinewzy.sunstcore.core.data.database.ColumnBuilder.() -> Unit): Table<T, E> {
        val builder = ColumnBuilder(name).also(tableBuilder)
        columns += builder.getColumn(type)
        return this
    }
    
}