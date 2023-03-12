package io.github.sunshinewzy.shining.core.data.container.persistence

import io.github.sunshinewzy.shining.core.data.DataManager
import io.github.sunshinewzy.shining.core.data.database.Database
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import taboolib.common.platform.function.submit

open class CachedLazySerialDataPersistentContainer<T>(
    serializer: KSerializer<T>,
    tableName: String,
    database: Database<*> = DataManager.sDatabase
) : LazySerialDataPersistentContainer<T>(serializer, tableName, database) {
    private val initCompleteSet = hashSetOf<String>()


    override fun init() {

    }


    override fun get(key: String): T? {
        init(key)
        return super.get(key)
    }


    fun init(key: String) {
        if (!initCompleteSet.contains(key)) {
            submit(async = true) {
                retrieve(key)?.let { dataMap[key] = Json.decodeFromString(serializer, it) }
            }
            initCompleteSet += key
        }
    }

}