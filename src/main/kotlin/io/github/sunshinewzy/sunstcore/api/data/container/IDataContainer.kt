package io.github.sunshinewzy.sunstcore.api.data.container

import io.github.sunshinewzy.sunstcore.api.NamespacedId
import io.github.sunshinewzy.sunstcore.api.data.IData
import io.github.sunshinewzy.sunstcore.core.data.container.DataContainerOptions

/**
 * Represent a container of data
 * 
 * 
 * 表示数据容器
 */
interface IDataContainer {
    
    val options: DataContainerOptions


    /**
     * Get the requested [IData] by [key].
     *
     * 
     * 通过 [key] 获取一个 [IData]。
     * 
     * @param key Key of the [IData] to get.
     * @return Requested [IData].
     */
    operator fun get(key: NamespacedId): IData

    
}