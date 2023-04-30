package io.github.sunshinewzy.shining.api.namespace

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.ShiningPlugin
import java.util.regex.Pattern


/**
 * Represent a String based id which consists of two components - a namespace
 * and an id.
 *
 * Namespace may only contain lowercase alphanumeric characters, periods,
 * underscores, and hyphens.
 *
 *
 * ID may only contain lowercase alphanumeric characters, periods,
 * underscores, hyphens, and forward slashes.
 *
 * @constructor Create a id in a specific namespace.
 * @param namespace namespace
 * @param id id
 */
data class NamespacedId(val namespace: Namespace, val id: String) : Cloneable {

    init {
        check(VALID_ID.matcher(id).matches()) {
            "Invalid id. Must be [a-z0-9/_-]: $id"
        }

        val string = toString()
        check(string.length < 256) {
            "NamespacedId must be less than 256 characters: $string"
        }
    }


    /**
     * Create an id in the plugin's namespace.
     *
     *
     * Namespace may only contain lowercase alphanumeric characters, periods,
     * underscores, and hyphens.
     *
     *
     * ID may only contain lowercase alphanumeric characters, periods,
     * underscores, hyphens, and forward slashes.
     *
     * @param plugin the plugin to use for the namespace
     * @param id the id to create
     */
    constructor(plugin: ShiningPlugin, id: String) : this(plugin.getNamespace(), id.lowercase())


    override fun hashCode(): Int {
        var hash = 5
        hash = 47 * hash + namespace.hashCode()
        hash = 47 * hash + id.hashCode()
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NamespacedId) return false

        if (namespace != other.namespace) return false
        if (id != other.id) return false

        return true
    }

    override fun toString(): String {
        return "${namespace.name}:$id"
    }


    companion object {
        val VALID_ID = Pattern.compile("[a-z0-9/_-]+")
        val NULL: NamespacedId by lazy { shining("null") }


        /**
         * Get an id in the shining namespace.
         *
         * @param id the id to use
         * @return new id in the shining namespace
         */
        fun shining(id: String): NamespacedId {
            return NamespacedId(Shining, id)
        }

        /**
         * Get a NamespacedId from the supplied string with a default namespace if
         * a namespace is not defined. This is a utility method meant to fetch a
         * NamespacedId from user input. Please note that casing does matter and
         * any instance of uppercase characters will be considered invalid. The
         * input contract is as follows:
         * <pre>
         * fromString("foo", plugin) -{@literal >} "plugin:foo"
         * fromString("foo:bar", plugin) -{@literal >} "foo:bar"
         * fromString(":foo", null) -{@literal >} "shining:foo"
         * fromString("foo", null) -{@literal >} "shining:foo"
         * fromString("Foo", plugin) -{@literal >} null
         * fromString(":Foo", plugin) -{@literal >} null
         * fromString("foo:bar:bazz", plugin) -{@literal >} null
         * fromString("", plugin) -{@literal >} null
         * </pre>
         *
         * @param string the string to convert to a NamespacedId
         * @param defaultNamespace the default namespace to use if none was
         * supplied. If null, the `shining` namespace will be used
         *
         * @return the created NamespacedId. null if invalid id
         * @see .fromString
         */
        fun fromString(string: String, defaultNamespace: ShiningPlugin?): NamespacedId? {
            check(string.isNotEmpty()) {
                "Input string must not be empty"
            }

            val components = string.split(":".toRegex(), limit = 3).toTypedArray()
            if (components.size > 2) {
                return null
            }

            val id = if (components.size == 2) components[1] else ""
            if (components.size == 1) {
                val value = components[0]
                return if (value.isEmpty() || !VALID_ID.matcher(value).matches()) {
                    null
                } else defaultNamespace?.let { NamespacedId(it, value) } ?: shining(value)
            } else if (components.size == 2 && !VALID_ID.matcher(id).matches()) {
                return null
            }

            val namespace = components[0]
            if (namespace.isEmpty()) {
                return defaultNamespace?.let { NamespacedId(it, id) } ?: shining(id)
            }
            return if (!Namespace.VALID_NAMESPACE.matcher(namespace).matches()) {
                null
            } else NamespacedId(Namespace[namespace], id)
        }

        /**
         * Get a NamespacedId from the supplied string.
         *
         * The default namespace will be shining.
         *
         * @param id the id to convert to a NamespacedId
         * @return the created NamespacedId. null if invalid
         * @see .fromString
         */
        fun fromString(id: String): NamespacedId? {
            return fromString(id, null)
        }
    }
}