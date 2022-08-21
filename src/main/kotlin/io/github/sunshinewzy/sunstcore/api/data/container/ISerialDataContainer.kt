package io.github.sunshinewzy.sunstcore.api.data.container

import io.github.sunshinewzy.sunstcore.api.data.ISerialDataRoot
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId

interface ISerialDataContainer : IDataContainer {

    /**
     * Get the requested [ISerialDataRoot] by [key].
     *
     *
     * 通过 [key] 获取一个 [ISerialDataRoot]。
     *
     * @param key Key of the [ISerialDataRoot] to get.
     * @return Requested [ISerialDataRoot].
     */
    override operator fun get(key: NamespacedId): ISerialDataRoot
    
}