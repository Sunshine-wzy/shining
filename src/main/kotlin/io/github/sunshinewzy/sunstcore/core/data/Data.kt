package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.api.data.IData
import io.github.sunshinewzy.sunstcore.api.data.IDataRoot
import java.util.concurrent.ConcurrentHashMap

open class Data : IData {
    final override val name: String
    final override val root: IDataRoot
    final override val parent: IData?
    
    
    protected val map = ConcurrentHashMap<String, Any>()
    
    
    @JvmOverloads
    constructor(name: String, root: IDataRoot, parent: IData? = null) {
        this.name = name
        this.root = root
        this.parent = parent
    }

    constructor(name: String, parent: IData) : this(name, parent.root, parent)
    
    internal constructor(name: String) {
        this.name = name
        @Suppress("LeakingThis")
        this.root = this as IDataRoot
        this.parent = null
    }
    

    override fun set(path: String, value: Any) {
        if(path.isEmpty()) return

        val separator = root.options.pathSeparator
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
        
        val separator = root.options.pathSeparator
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

        val separator = root.options.pathSeparator
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

        val separator = root.options.pathSeparator
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
        return LinkedHashSet<String>().also {
            mapChildrenKeys(it, this, deep)
        }
    }

    override fun getValues(deep: Boolean): Map<String, Any> {
        return LinkedHashMap<String, Any>().also { 
            mapChildrenValues(it, this, deep)
        }
    }

    override fun contains(path: String): Boolean {
        return get(path) != null
    }

    override fun getString(path: String): String? {
        TODO("Not yet implemented")
    }

    override fun getString(path: String, default: String): String {
        TODO("Not yet implemented")
    }

    override fun isString(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getInt(path: String): Int {
        TODO("Not yet implemented")
    }

    override fun getInt(path: String, default: Int): Int {
        TODO("Not yet implemented")
    }

    override fun isInt(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBoolean(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBoolean(path: String, default: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun isBoolean(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getDouble(path: String): Double {
        TODO("Not yet implemented")
    }

    override fun getDouble(path: String, default: Double): Double {
        TODO("Not yet implemented")
    }

    override fun isDouble(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLong(path: String): Long {
        TODO("Not yet implemented")
    }

    override fun getLong(path: String, default: Long): Long {
        TODO("Not yet implemented")
    }

    override fun isLong(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getList(path: String): List<*> {
        TODO("Not yet implemented")
    }

    override fun getList(path: String, default: List<*>): List<*> {
        TODO("Not yet implemented")
    }

    override fun isList(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getStringList(path: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun getIntList(path: String): List<Int> {
        TODO("Not yet implemented")
    }

    override fun getBooleanList(path: String): List<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getDoubleList(path: String): List<Double> {
        TODO("Not yet implemented")
    }

    override fun getFloatList(path: String): List<Float> {
        TODO("Not yet implemented")
    }

    override fun getLongList(path: String): List<Long> {
        TODO("Not yet implemented")
    }

    override fun getByteList(path: String): List<Byte> {
        TODO("Not yet implemented")
    }

    override fun getCharList(path: String): List<Char> {
        TODO("Not yet implemented")
    }

    override fun getShortList(path: String): List<Short> {
        TODO("Not yet implemented")
    }

    override fun getMapList(path: String): List<Map<*, *>> {
        TODO("Not yet implemented")
    }
    

    fun mapChildrenKeys(output: MutableSet<String>, relativeTo: IData, deep: Boolean) {
        map.forEach { (key, value) -> 
            output += IData.createPath(this, key, relativeTo)
            
            if(deep && value is Data) {
                value.mapChildrenKeys(output, relativeTo, true)
            }
        }
    }
    
    fun mapChildrenValues(output: MutableMap<String, Any>, relativeTo: IData, deep: Boolean) {
        map.forEach { (key, value) -> 
            output[IData.createPath(this, key, relativeTo)] = value
            
            if(deep && value is Data) {
                value.mapChildrenValues(output, relativeTo, true)
            }
        }
    }
    
    
}