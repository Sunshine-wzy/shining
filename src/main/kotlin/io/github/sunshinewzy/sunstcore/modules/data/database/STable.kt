package io.github.sunshinewzy.sunstcore.modules.data.database

import taboolib.module.database.ColumnBuilder
import taboolib.module.database.Host
import taboolib.module.database.Table

@Suppress("LeakingThis")
open class STable<T: Host<E>, E: ColumnBuilder>(
    name: String,
    host: Host<E>,
    val type: DatabaseType,
    func: STable<T, E>.() -> Unit = {}
) : Table<T, E>(name, host) {
    
    init {
        func(this)
    }
    
    
    open fun build(name: String? = null, tableBuilder: SColumnBuilder.() -> Unit): Table<T, E> {
        val builder = SColumnBuilder(name).also(tableBuilder)
        columns += builder.getColumn(type)
        return this
    }
    
}