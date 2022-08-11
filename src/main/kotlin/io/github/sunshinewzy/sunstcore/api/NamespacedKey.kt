package io.github.sunshinewzy.sunstcore.api

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.core.data.serializer.NamespacedKeySerializer
import kotlinx.serialization.Serializable
import java.util.regex.Pattern


/**
 * Represent a String based key which consists of two components - a namespace
 * and a key.
 *
 * Namespaces may only contain lowercase alphanumeric characters, periods,
 * underscores, and hyphens.
 *
 *
 * Keys may only contain lowercase alphanumeric characters, periods,
 * underscores, hyphens, and forward slashes.
 *
 * @constructor Create a key in a specific namespace.
 * @param namespace namespace
 * @param key key
 */
@Serializable(NamespacedKeySerializer::class)
class NamespacedKey(val namespace: Namespace, val key: String) {

    init {
        check(VALID_KEY.matcher(key).matches()) {
            "Invalid key. Must be [a-z0-9/._-]: $key"
        }
        
        val string = toString()
        check(string.length < 256) {
            "NamespacedKey must be less than 256 characters: $string"
        }
    }
    
    
    /**
     * Create a key in the plugin's namespace.
     *
     *
     * Namespaces may only contain lowercase alphanumeric characters, periods,
     * underscores, and hyphens.
     *
     *
     * Keys may only contain lowercase alphanumeric characters, periods,
     * underscores, hyphens, and forward slashes.
     *
     * @param plugin the plugin to use for the namespace
     * @param key the key to create
     */
    constructor(plugin: SPlugin, key: String) : this(plugin.getNamespace(), key.lowercase())
    

    override fun hashCode(): Int {
        var hash = 5
        hash = 47 * hash + namespace.hashCode()
        hash = 47 * hash + key.hashCode()
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is NamespacedKey) return false

        if(namespace != other.namespace) return false
        if(key != other.key) return false

        return true
    }

    override fun toString(): String {
        return "${namespace.name}:$key"
    }
    

    companion object {
        val VALID_KEY = Pattern.compile("[a-z0-9/._-]+")
        val NULL = sunstcore("null")
        

        /**
         * Get a key in the sunstcore namespace.
         *
         * @param key the key to use
         * @return new key in the sunstcore namespace
         */
        fun sunstcore(key: String): NamespacedKey {
            return NamespacedKey(SunSTCore, key)
        }

        /**
         * Get a NamespacedKey from the supplied string with a default namespace if
         * a namespace is not defined. This is a utility method meant to fetch a
         * NamespacedKey from user input. Please note that casing does matter and
         * any instance of uppercase characters will be considered invalid. The
         * input contract is as follows:
         * <pre>
         * fromString("foo", plugin) -{@literal >} "plugin:foo"
         * fromString("foo:bar", plugin) -{@literal >} "foo:bar"
         * fromString(":foo", null) -{@literal >} "sunstcore:foo"
         * fromString("foo", null) -{@literal >} "sunstcore:foo"
         * fromString("Foo", plugin) -{@literal >} null
         * fromString(":Foo", plugin) -{@literal >} null
         * fromString("foo:bar:bazz", plugin) -{@literal >} null
         * fromString("", plugin) -{@literal >} null
         * </pre>
         *
         * @param string the string to convert to a NamespacedKey
         * @param defaultNamespace the default namespace to use if none was
         * supplied. If null, the `sunstcore` namespace will be used
         * 
         * @return the created NamespacedKey. null if invalid key
         * @see .fromString
         */
        fun fromString(string: String, defaultNamespace: SPlugin?): NamespacedKey? {
            check(string.isNotEmpty()) {
                "Input string must not be empty"
            }
            
            val components = string.split(":".toRegex(), limit = 3).toTypedArray()
            if(components.size > 2) {
                return null
            }
            
            val key = if(components.size == 2) components[1] else ""
            if(components.size == 1) {
                val value = components[0]
                return if(value.isEmpty() || !VALID_KEY.matcher(value).matches()) {
                    null
                } else defaultNamespace?.let { NamespacedKey(it, value) } ?: sunstcore(value)
            } else if(components.size == 2 && !VALID_KEY.matcher(key).matches()) {
                return null
            }
            
            val namespace = components[0]
            if(namespace.isEmpty()) {
                return defaultNamespace?.let { NamespacedKey(it, key) } ?: sunstcore(key)
            }
            return if(!Namespace.VALID_NAMESPACE.matcher(namespace).matches()) {
                null
            } else NamespacedKey(Namespace.get(namespace), key)
        }

        /**
         * Get a NamespacedKey from the supplied string.
         *
         * The default namespace will be sunstcore.
         *
         * @param key the key to convert to a NamespacedKey
         * @return the created NamespacedKey. null if invalid
         * @see .fromString
         */
        fun fromString(key: String): NamespacedKey? {
            return fromString(key, null)
        }
    }
}