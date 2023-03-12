package io.github.sunshinewzy.shining.core.data.database

import taboolib.module.database.ColumnBuilder
import taboolib.module.database.Host
import taboolib.module.database.Table
import io.github.sunshinewzy.shining.core.data.database.Table as STable

@Suppress("LeakingThis")
open class Table<T : Host<E>, E : ColumnBuilder>(
    name: String,
    host: Host<E>,
    val type: DatabaseType,
    func: STable<T, E>.() -> Unit = {}
) : Table<T, E>(name, host) {

    init {
        func(this)
    }


    open fun build(
        name: String? = null,
        tableBuilder: io.github.sunshinewzy.shining.core.data.database.ColumnBuilder.() -> Unit
    ): Table<T, E> {
        val builder = ColumnBuilder(name).also(tableBuilder)
        columns += builder.getColumn(type)
        return this
    }

}