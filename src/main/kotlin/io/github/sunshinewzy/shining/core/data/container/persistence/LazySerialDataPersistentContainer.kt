package io.github.sunshinewzy.shining.core.data.container.persistence

import io.github.sunshinewzy.shining.core.data.DataManager
import io.github.sunshinewzy.shining.core.data.LazyOperation
import io.github.sunshinewzy.shining.core.data.LazyOperational
import io.github.sunshinewzy.shining.core.data.database.Database
import kotlinx.serialization.KSerializer
import java.util.concurrent.ConcurrentHashMap

open class LazySerialDataPersistentContainer<T>(
    serializer: KSerializer<T>,
    tableName: String,
    database: Database<*> = DataManager.sDatabase
) : SerialDataPersistentContainer<T>(serializer, tableName, database), LazyOperational {
    private val lazyOperationMap: ConcurrentHashMap<String, LazyOperation> = ConcurrentHashMap()


    init {
        DataManager.registerLazy(this)
    }


    override operator fun set(key: String, value: T) {
        dataMap[key] = value
        lazyOperationMap[key] = LazyOperation.Update(value)
    }

    override fun remove(key: String) {
        dataMap -= key
        lazyOperationMap[key] = LazyOperation.Delete
    }

    @Suppress("UNCHECKED_CAST")
    override fun saveLazy() {
        lazyOperationMap.forEach { (key, operation) ->
            when (operation) {
                is LazyOperation.Update<*> -> {
                    update(key, operation.value as T)
                }

                LazyOperation.Delete -> {
                    delete(key)
                }
            }

            lazyOperationMap -= key
        }
    }
}