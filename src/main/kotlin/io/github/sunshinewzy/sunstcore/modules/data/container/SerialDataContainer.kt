package io.github.sunshinewzy.sunstcore.modules.data.container

import io.github.sunshinewzy.sunstcore.modules.data.DataManager
import io.github.sunshinewzy.sunstcore.modules.data.database.SDatabase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoBuf
import taboolib.common.platform.function.submit
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnOptionSQLite
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.ColumnTypeSQLite
import java.util.concurrent.ConcurrentHashMap

@OptIn(ExperimentalSerializationApi::class)
open class SerialDataContainer<T>(
    val serializer: KSerializer<T>,
    val tableName: String,
    val database: SDatabase<*> = DataManager.database
) {
    protected val table = database.get(tableName) {
        build("key") { 
            sql { 
                type(ColumnTypeSQL.VARCHAR) {
                    options(ColumnOptionSQL.PRIMARY_KEY)
                }
            }
            
            sqlite { 
                type(ColumnTypeSQLite.TEXT) {
                    options(ColumnOptionSQLite.PRIMARY_KEY)
                }
            }
        }
        
        build("value") {
            sql { 
                type(ColumnTypeSQL.BLOB)
            }
            
            sqlite { 
                type(ColumnTypeSQLite.BLOB)
            }
        }
    }
    
    protected val dataMap = ConcurrentHashMap<String, T>()
    
    
    init {
        init()
    }
    
    
    open fun init() {
        dataMap += retrieve()
    }
    
    open operator fun get(key: String): T? {
        return dataMap[key]
    }
    
    open operator fun set(key: String, value: T) {
        dataMap[key] = value
        save(key)
    }
    
    open fun remove(key: String) {
        dataMap -= key
        
        submit(async = true) { 
            delete(key)
        }
    }
    
    
    fun save(key: String) {
        val obj = dataMap[key] ?: return
        
        submit(async = true) {
            update(key, obj)
        }
    }
    
    operator fun minusAssign(key: String) {
        remove(key)
    }
    
    fun containsKey(key: String): Boolean =
        dataMap.containsKey(key)
    
    // CRUD
    fun retrieve(): MutableMap<String, T> {
        return table.select(database.dataSource) {
            rows("key", "value")
        }.map {
            getString("key") to ProtoBuf.decodeFromByteArray(serializer, getBytes("value"))
        }.toMap(hashMapOf())
    }

    fun retrieve(key: String): ByteArray? {
        return table.select(database.dataSource) {
            rows("key", "value")
            where("key" eq key)
            limit(1)
        }.firstOrNull { 
            getBytes("value")
        }
    }
    
    fun update(key: String, value: T) {
        val bytes = ProtoBuf.encodeToByteArray(serializer, value)
        
        if(retrieve(key) == null) {
            table.insert(database.dataSource, "key", "value") {
                value(key, bytes)
            }
        } else {
            table.update(database.dataSource) {
                set("value", bytes)
                where("key" eq key)
            }
        }
    }
    
    fun delete(key: String) {
        table.delete(database.dataSource) {
            where("key" eq key)
        }
    }
    
    
    private fun reload() {
        dataMap.clear()
        dataMap += retrieve()
    }
}