package io.github.sunshinewzy.sunstcore.core.data.container

import io.github.sunshinewzy.sunstcore.api.data.IDataRoot
import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.core.data.DataRoot
import java.util.concurrent.ConcurrentHashMap

class DataContainer : IDataContainer {
    private val map = ConcurrentHashMap<NamespacedId, IDataRoot>()
    

    override fun get(key: NamespacedId): IDataRoot {
        map[key]?.let { return it }
        
        return DataRoot(key.id, this).also { map[key] = it }
    }
    
}