package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.api.data.IData
import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer
import java.util.concurrent.ConcurrentHashMap

class Data(override val container: IDataContainer) : IData {
    private val map = ConcurrentHashMap<String, Any>()
    
    override val parent: IData? = null
    
    
    override fun set(path: String, value: Any) {
        
        
        map[path] = value
    }

    override fun get(path: String): Any? {
        if(path.isEmpty()) return this
        
        if(path.startsWith(container.options.ignorePathSeparator)) {
            return map[path]
        }
        
        val separator = container.options.pathSeparator
        // `i` is the leading (higher) index
        // `j` is the trailing (lower) index
        var i = -1
        var j: Int

        var data: IData = this
        while(path.indexOf(separator, (i + 1).also { j = it }).also { i = it } != -1) {
            val currentPath = path.substring(j, i)
            data = data.getData(currentPath) ?: return null
        }
        
        val key = path.substring(j)
        if(data === this) {
            return map[key]
        }
        
        return data[key]
    }

    override fun get(path: String, default: Any): Any {
        return get(path) ?: default
    }

    override fun <T> getWithType(path: String, type: Class<T>): T? {
        get(path)?.let { 
            if(type.isInstance(it)) {
                @Suppress("UNCHECKED_CAST")
                return it as T
            }
        }
        
        return null
    }

    override fun <T> getWithType(path: String, type: Class<T>, default: T): T {
        return getWithType(path, type) ?: default
    }

    override fun getData(path: String): IData? {
        return getWithType(path, IData::class.java)
    }
}