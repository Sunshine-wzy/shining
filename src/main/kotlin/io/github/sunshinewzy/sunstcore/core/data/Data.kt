package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.api.data.IData
import io.github.sunshinewzy.sunstcore.api.data.container.IDataContainer
import java.util.concurrent.ConcurrentHashMap

open class Data(
    override val name: String,
    override val container: IDataContainer,
    override val parent: IData? = null
) : IData {
    protected val map = ConcurrentHashMap<String, Any>()


    constructor(name: String, parent: IData) : this(name, parent.container, parent)


    override fun set(path: String, value: Any) {
        if(path.isEmpty()) return

        if(path.startsWith(container.options.ignorePathSeparator)) {
            map[path.substring(1)] = value
        }

        val separator = container.options.pathSeparator
        // `i` is the leading (higher) index
        // `j` is the trailing (lower) index
        var i = -1
        var j: Int

        var data: IData = this
        while(path.indexOf(separator, (i + 1).also { j = it }).also { i = it } != -1) {
            val currentPath = path.substring(j, i)
            data = data.getData(currentPath) ?: data.createData(currentPath)
        }
        
        val key = path.substring(j)
        if(data === this) {
            map[key] = value
            return
        }
        
        data[key] = value
    }

    override fun get(path: String): Any? {
        if(path.isEmpty()) return this
        
        if(path.startsWith(container.options.ignorePathSeparator)) {
            return map[path.substring(1)]
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

    override fun createData(path: String): IData {
        if(path.isEmpty()) return this

        if(path.startsWith(container.options.ignorePathSeparator)) {
            val key = path.substring(1)
            return Data(key, this).also { map[key] = it }
        }

        val separator = container.options.pathSeparator
        // `i` is the leading (higher) index
        // `j` is the trailing (lower) index
        var i = -1
        var j: Int

        var data: IData = this
        while(path.indexOf(separator, (i + 1).also { j = it }).also { i = it } != -1) {
            val currentPath = path.substring(j, i)
            data = data.getData(currentPath) ?: data.createData(currentPath)
        }

        val key = path.substring(j)
        if(data === this) {
            return Data(key, this).also { map[key] = it }
        }

        return data.createData(key)
    }

    override fun remove(path: String) {
        if(path.isEmpty()) return

        if(path.startsWith(container.options.ignorePathSeparator)) {
            map -= path.substring(1)
        }

        val separator = container.options.pathSeparator
        // `i` is the leading (higher) index
        // `j` is the trailing (lower) index
        var i = -1
        var j: Int

        var data: IData = this
        while(path.indexOf(separator, (i + 1).also { j = it }).also { i = it } != -1) {
            val currentPath = path.substring(j, i)
            data = data.getData(currentPath) ?: return
        }

        val key = path.substring(j)
        if(data === this) {
            map -= key
            return
        }

        data.remove(key)
    }

    override fun getKeys(deep: Boolean): Set<String> {
        val result = LinkedHashSet<String>()
        
        mapChildrenKeys(result, this, deep)
        
        return result
    }

    override fun getValues(deep: Boolean): Map<String?, Any?> {
        TODO("Not yet implemented")
    }

    override fun contains(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(path: String, ignoreDefault: Boolean): Boolean {
        TODO("Not yet implemented")
    }
    
    
    fun mapChildrenKeys(output: MutableSet<String>, data: Any, deep: Boolean) {
        if(data is IData) {
            if(data === this) {
                output += map.keys
            }
        }
        
        
    }
    
    
}