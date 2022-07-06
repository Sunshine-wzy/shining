package io.github.sunshinewzy.sunstcore.objects

class SHashMap<K, V> : HashMap<K, V>() {
    
    fun set(key: K, value: V): SHashMap<K, V> {
        put(key, value)
        return this
    }
    
}