package io.github.sunshinewzy.sunstcore.modules.data.container

import io.github.sunshinewzy.sunstcore.modules.data.DataManager
import io.github.sunshinewzy.sunstcore.modules.data.database.SDatabase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import taboolib.common.platform.function.submit

@OptIn(ExperimentalSerializationApi::class)
open class OptionalLazySerialDataContainer<T>(
    serializer: KSerializer<T>,
    tableName: String,
    database: SDatabase<*> = DataManager.database
) : LazySerialDataContainer<T>(serializer, tableName, database) {
    private val initCompleteSet = hashSetOf<String>()
    
    
    override fun init() {
        
    }


    override fun get(key: String): T? {
        init(key)
        return super.get(key)
    }
    
    
    fun init(key: String) {
        if(!initCompleteSet.contains(key)) {
            submit(async = true) {
                retrieve(key)?.let { dataMap[key] = Json.decodeFromString(serializer, it) }
            }
            initCompleteSet += key
        }
    }
    
}