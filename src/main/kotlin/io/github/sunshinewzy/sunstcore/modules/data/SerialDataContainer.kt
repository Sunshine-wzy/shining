package io.github.sunshinewzy.sunstcore.modules.data

import io.github.sunshinewzy.sunstcore.modules.data.DataManager.database
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
class SerialDataContainer<T: KeySerializable>(val serializer: KSerializer<T>, val tableName: String) {
    private val table = database.get(tableName) {
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
    
    private val dataMap = ConcurrentHashMap<String, T>()
    
    
    init {
        dataMap += retrieve()
    }
    
    
    operator fun get(key: String): T? {
        return dataMap[key]
    }
    
    operator fun set(key: String, value: T) {
        dataMap[key] = value
        save(key)
    }
    
    fun save(key: String) {
        val obj = dataMap[key] ?: return
        
        submit(async = true) {
            val value = ProtoBuf.encodeToByteArray(serializer, obj)
            
            if(retrieve(key) == null) {
                table.insert(database.dataSource, "key", "value") {
                    value(key, value)
                }
            } else {
                table.update(database.dataSource) {
                    set("value", value)
                    where("key" eq key)
                }
            }
        }
    }
    
    fun containsKey(key: String): Boolean =
        dataMap.containsKey(key)
    
    
    private fun retrieve(): MutableMap<String, T> {
        return table.select(database.dataSource) {
            rows("key", "value")
        }.map {
            getString("key") to ProtoBuf.decodeFromByteArray(serializer, getBytes("value"))
        }.toMap(hashMapOf())
    }

    private fun retrieve(key: String): ByteArray? {
        return table.select(database.dataSource) {
            rows("key", "value")
            where("key" eq key)
            limit(1)
        }.firstOrNull { 
            getBytes("value")
        }
    }
    
    private fun reload() {
        dataMap.clear()
        dataMap += retrieve()
    }
}