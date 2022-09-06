package io.github.sunshinewzy.sunstcore.core.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.sunshinewzy.sunstcore.api.data.IData
import io.github.sunshinewzy.sunstcore.api.data.ISerialData
import io.github.sunshinewzy.sunstcore.api.data.ISerialDataRoot
import io.github.sunshinewzy.sunstcore.utils.Coerce
import io.github.sunshinewzy.sunstcore.utils.asPrimitiveOrNull
import io.github.sunshinewzy.sunstcore.utils.putPrimitive
import java.util.concurrent.ConcurrentHashMap


open class SerialData : ISerialData {
    final override val name: String
    final override val root: ISerialDataRoot
    final override val parent: ISerialData?

    override val objectMapper: ObjectMapper = jacksonObjectMapper()

    protected val map: MutableMap<String, SerialDataWrapper<*>> = ConcurrentHashMap()
    
    
    @JvmOverloads
    constructor(name: String, root: ISerialDataRoot, parent: ISerialData? = null) {
        this.name = name
        this.root = root
        this.parent = parent
    }
    
    constructor(name: String, parent: ISerialData) : this(name, parent.root, parent)
    
    internal constructor(name: String) {
        this.name = name
        @Suppress("LeakingThis")
        this.root = this as ISerialDataRoot
        this.parent = null
    }


    override fun set(path: String, value: Any) {
        if(path.isEmpty()) return

        val separator = root.options.pathSeparator
        // `i` is the leading (higher) index
        // `j` is the trailing (lower) index
        var i = -1
        var j: Int

        var data: ISerialData = this
        while(path.indexOf(separator, (i + 1).also { j = it }).also { i = it } != -1) {
            val currentPath = path.substring(j, i)
            data = data.getData(currentPath) ?: data.createData(currentPath)
        }

        val key = path.substring(j)
        if(data === this) {
            map[key] = SerialDataWrapper(value)
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

        var data: ISerialData = this
        while(path.indexOf(separator, (i + 1).also { j = it }).also { i = it } != -1) {
            val currentPath = path.substring(j, i)
            data = data.getData(currentPath) ?: return null
        }

        val key = path.substring(j)
        if(data === this) {
            return map[key]?.data
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

    override fun getData(path: String): ISerialData? {
        return getWithType(path, ISerialData::class.java)
    }

    override fun createData(path: String): ISerialData {
        if(path.isEmpty()) return this

        val separator = root.options.pathSeparator
        // `i` is the leading (higher) index
        // `j` is the trailing (lower) index
        var i = -1
        var j: Int

        var data: ISerialData = this
        while(path.indexOf(separator, (i + 1).also { j = it }).also { i = it } != -1) {
            val currentPath = path.substring(j, i)
            data = data.getData(currentPath) ?: data.createData(currentPath)
        }

        val key = path.substring(j)
        if(data === this) {
            return SerialData(key, this).also { map[key] = SerialDataWrapper(it) }
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

        var data: ISerialData = this
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

    override fun clear() {
        map.clear()
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
        return get(path)?.toString()
    }

    override fun getString(path: String, default: String): String {
        return getString(path) ?: default
    }

    override fun isString(path: String): Boolean {
        return get(path) is String
    }

    override fun getInt(path: String): Int {
        return Coerce.toInteger(get(path))
    }

    override fun getInt(path: String, default: Int): Int {
        val value = get(path) ?: return default
        return Coerce.toInteger(value)
    }

    override fun isInt(path: String): Boolean {
        return get(path) is Int
    }

    override fun getBoolean(path: String): Boolean {
        return Coerce.toBoolean(get(path))
    }

    override fun getBoolean(path: String, default: Boolean): Boolean {
        val value = get(path) ?: return default
        return Coerce.toBoolean(value)
    }

    override fun isBoolean(path: String): Boolean {
        return get(path) is Boolean
    }

    override fun getDouble(path: String): Double {
        return Coerce.toDouble(get(path))
    }

    override fun getDouble(path: String, default: Double): Double {
        val value = get(path) ?: return default
        return Coerce.toDouble(value)
    }

    override fun isDouble(path: String): Boolean {
        return get(path) is Double
    }

    override fun getLong(path: String): Long {
        return Coerce.toLong(get(path))
    }

    override fun getLong(path: String, default: Long): Long {
        val value = get(path) ?: return default
        return Coerce.toLong(value)
    }

    override fun isLong(path: String): Boolean {
        return get(path) is Long
    }

    override fun getList(path: String): List<*>? {
        return get(path) as? List<*>
    }

    override fun getList(path: String, default: List<*>): List<*> {
        return getList(path) ?: default
    }

    override fun isList(path: String): Boolean {
        return get(path) is List<*>
    }

    override fun getStringList(path: String): List<String> {
        return getList(path)?.map { it.toString() } ?: emptyList()
    }

    override fun getIntList(path: String): List<Int> {
        return getList(path)?.map { Coerce.toInteger(it) } ?: emptyList()
    }

    override fun getBooleanList(path: String): List<Boolean> {
        return getList(path)?.map { Coerce.toBoolean(it) } ?: emptyList()
    }

    override fun getDoubleList(path: String): List<Double> {
        return getList(path)?.map { Coerce.toDouble(it) } ?: emptyList()
    }

    override fun getFloatList(path: String): List<Float> {
        return getList(path)?.map { Coerce.toFloat(it) } ?: emptyList()
    }

    override fun getLongList(path: String): List<Long> {
        return getList(path)?.map { Coerce.toLong(it) } ?: emptyList()
    }

    override fun getByteList(path: String): List<Byte> {
        return getList(path)?.map { Coerce.toByte(it) } ?: emptyList()
    }

    override fun getCharList(path: String): List<Char> {
        return getList(path)?.map { Coerce.toChar(it) } ?: emptyList()
    }

    override fun getShortList(path: String): List<Short> {
        return getList(path)?.map { Coerce.toShort(it) } ?: emptyList()
    }

    override fun getMapList(path: String): List<Map<*, *>> {
        return getList(path)?.filterIsInstance<Map<*, *>>() ?: emptyList()
    }


    fun mapChildrenKeys(output: MutableSet<String>, relativeTo: ISerialData, deep: Boolean) {
        map.forEach { (key, wrapper) ->
            output += IData.createPath(this, key, relativeTo)
            
            val value = wrapper.data ?: return@forEach
            if(deep && value is SerialData) {
                value.mapChildrenKeys(output, relativeTo, true)
            }
        }
    }

    fun mapChildrenValues(output: MutableMap<String, Any>, relativeTo: ISerialData, deep: Boolean) {
        map.forEach { (key, wrapper) ->
            val value = wrapper.data ?: return@forEach
            output[IData.createPath(this, key, relativeTo)] = value

            if(deep && value is SerialData) {
                value.mapChildrenValues(output, relativeTo, true)
            }
        }
    }


    override fun serializeToJsonNode(): JsonNode {
        val node = objectMapper.createObjectNode()
        map.forEach { (key, wrapper) -> 
            val obj = wrapper.data ?: return@forEach
            
            if(obj is ISerialData) {
                val newNode = node.putObject(key)
                newNode.put(KEY_TYPE, SERIAL_DATA)
                newNode.replace(KEY_DATA, obj.serializeToJsonNode())
                return@forEach
            }
            
            if(node.putPrimitive(key, obj)) {
                return@forEach
            }
            
            val newNode = node.putObject(key)
            newNode.put(KEY_TYPE, obj::class.java.name)
            newNode.putPOJO(KEY_DATA, obj)
        }
        
        return node
    }

    override fun serializeToString(): String {
        return serializeToJsonNode().toString()
    }

    override fun deserialize(source: JsonNode) {
        if(source is ObjectNode) {
            source.fields().forEach { (key, node) ->
                if(node is ObjectNode) {
                    val type = node.get(KEY_TYPE)
                        ?.takeIf { it.isTextual }?.textValue()
                        ?.takeIf { it.isNotBlank() } ?: return@forEach

                    if(type == SERIAL_DATA) {
                        val data = node.get(KEY_DATA) ?: return@forEach
                        createData(key).deserialize(data)
                        
                        return@forEach
                    }

                    kotlin.runCatching {
                        Class.forName(type)
                    }.onSuccess { clazz ->
                        val data = node.get(KEY_DATA) ?: return@forEach
                        map[key] = SerialDataWrapper(objectMapper.treeToValue(data, clazz))
                    }
                } else {
                    node.asPrimitiveOrNull()?.let {
                        map[key] = SerialDataWrapper(it)
                    }
                }
            }
        }
    }

    override fun deserialize(source: String) {
        deserialize(objectMapper.readTree(source))
    }
    
    
    companion object {
        const val KEY_TYPE = "s_type"
        const val KEY_DATA = "s_data"
        const val SERIAL_DATA = "SerialData"
    }
    
}