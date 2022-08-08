package io.github.sunshinewzy.sunstcore.api

import java.util.concurrent.ConcurrentHashMap

class Namespace private constructor(val name: String) {

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
        return "Namespace(name='$name')"
    }


    companion object {

        private val cache = ConcurrentHashMap<String, Namespace>()

        
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