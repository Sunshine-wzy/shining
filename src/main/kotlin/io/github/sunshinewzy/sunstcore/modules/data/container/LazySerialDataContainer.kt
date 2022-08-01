package io.github.sunshinewzy.sunstcore.modules.data.container

import io.github.sunshinewzy.sunstcore.modules.data.DataManager
import io.github.sunshinewzy.sunstcore.modules.data.LazyOperation
import io.github.sunshinewzy.sunstcore.modules.data.LazyOperational
import io.github.sunshinewzy.sunstcore.modules.data.database.SDatabase
import kotlinx.serialization.KSerializer
import java.util.concurrent.ConcurrentHashMap

open class LazySerialDataContainer<T>(
    serializer: KSerializer<T>,
    tableName: String,
    database: SDatabase<*> = DataManager.database
) : SerialDataContainer<T>(serializer, tableName, database), LazyOperational {
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
            when(operation) {
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