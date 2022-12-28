package io.github.sunshinewzy.shining.objects

class SHashMap<K, V> : HashMap<K, V>() {
    
    fun set(key: K, value: V): SHashMap<K, V> {
        put(key, value)
        return this
    }
    
}