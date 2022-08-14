package io.github.sunshinewzy.sunstcore.core.data.container

import io.github.sunshinewzy.sunstcore.api.NamespacedId
import io.github.sunshinewzy.sunstcore.api.data.IData
import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer
import io.github.sunshinewzy.sunstcore.core.data.Data
import java.util.concurrent.ConcurrentHashMap

class DataContainer : IDataContainer {
    private val map = ConcurrentHashMap<NamespacedId, IData>()
    

    override val options: DataContainerOptions = DataContainerOptions()


    override fun get(key: NamespacedId): IData {
        map[key]?.let { return it }
        
        return Data(this).also { map[key] = it }
    }
    
}