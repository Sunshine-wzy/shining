package io.github.sunshinewzy.shining.core.data.container

import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.data.DataRoot
import io.github.sunshinewzy.shining.core.data.IDataRoot
import java.util.concurrent.ConcurrentHashMap

class DataContainer : IDataContainer {
    private val map = ConcurrentHashMap<NamespacedId, IDataRoot>()


    override fun get(key: NamespacedId): IDataRoot {
        map[key]?.let { return it }

        return DataRoot(key.id, this).also { map[key] = it }
    }

}