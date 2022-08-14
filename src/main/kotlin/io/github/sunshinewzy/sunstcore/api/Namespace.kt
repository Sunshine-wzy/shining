package io.github.sunshinewzy.sunstcore.api

import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

/**
 * The [name] of namespace may only contain lowercase alphanumeric characters, periods,
 * underscores, and hyphens.
 */
@Serializable
class Namespace private constructor(val name: String) {

    init {
        check(VALID_NAMESPACE.matcher(name).matches()) {
            "Invalid namespace. Must be [a-z0-9._-]: $name"
        }
    }
    
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Namespace) return false

        if(name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }


    companion object {
        private val cache = ConcurrentHashMap<String, Namespace>()
        
        val VALID_NAMESPACE = Pattern.compile("[a-z0-9._-]+")

        
        @JvmStatic
        fun get(name: String): Namespace {
            cache[name]?.let { 
                return it
            }
            
            val namespace = Namespace(name)
            cache[name] = namespace
            return namespace
        }

    }

}