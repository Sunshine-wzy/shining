package io.github.sunshinewzy.sunstcore.core.data

import io.github.sunshinewzy.sunstcore.api.data.IData
import io.github.sunshinewzy.sunstcore.api.data.IDataRoot
import io.github.sunshinewzy.sunstcore.api.data.ISerialData
import io.github.sunshinewzy.sunstcore.utils.Coerce
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import java.util.concurrent.ConcurrentHashMap

/**
 * ## 序列化优先级
 * 
 * 类注解提供的kotlinx.serialization序列化器 > ISerialData中提供的kotlinx.serialization序列化器 >
 * ISerialData中提供的gson序列化器 > 默认的kotlinx.serialization序列化器 > gson反射自动序列化
 */
open class SerialData : ISerialData {
    final override val name: String
    final override val root: IDataRoot
    final override val parent: ISerialData?


    protected val map = ConcurrentHashMap<String, Any>()
    
    
    private val serialMap = ConcurrentHashMap<Class<*>, KSerializer<*>>()
    
    
    @JvmOverloads
    constructor(name: String, root: IDataRoot, parent: ISerialData? = null) {
        this.name = name
        this.root = root
        this.parent = parent
    }
    
    constructor(name: String, parent: ISerialData) : this(name, parent.root, parent)
    
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


    override fun serializeToString(): String {
        return Json.encodeToString(serializer, this)
    }

    override fun serializeToJsonElement(): JsonElement {
        return Json.encodeToJsonElement(serializer, this)
    }

    override fun deserialize(source: String) {
        Json.decodeFromString(serializer, source)
    }
    
    
    companion object {
        val serializer = object : KSerializer<SerialData> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SerialData", PrimitiveKind.STRING)

            
            override fun serialize(encoder: Encoder, value: SerialData) {
                val json = buildJsonObject {
                    value.map.forEach { (key, obj) ->
                        if(obj is ISerialData) {
                            put(key, obj.serializeToJsonElement())
                            return@forEach
                        }
                        
                        
                    }
                }
                
                encoder.encodeString(json.toString())
                TODO()
            }

            override fun deserialize(decoder: Decoder): SerialData {
                
//                decoder.decodeString()
                TODO()
            }
            
        }
    }
    
}