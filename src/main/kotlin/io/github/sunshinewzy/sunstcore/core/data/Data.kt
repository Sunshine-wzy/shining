package io.github.sunshinewzy.sunstcore.core.data

import java.util.concurrent.ConcurrentHashMap

class Data : IData {
    private val map = ConcurrentHashMap<String, Any>()

    override val parent: IData? = null
    
    
    override fun set(path: String, value: Any) {
        
        
        map[path] = value
    }

    override fun get(path: String): Any? {
        if(path.isEmpty()) return this
        
        
        
        return map[path]
    }

    override fun get(path: String, default: Any): Any {
        return map[path] ?: default
    }
}