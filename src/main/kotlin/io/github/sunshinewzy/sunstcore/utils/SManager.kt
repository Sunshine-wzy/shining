package io.github.sunshinewzy.sunstcore.utils

import java.util.*

object SManager {

    // Object转List
    fun <T> castList(obj: Any?, clazz: Class<T>): List<T>? {
        val result: MutableList<T> = ArrayList()
        if(obj != null && obj is List<*>) {
            for(o in obj) {
                result.add(clazz.cast(o))
            }
            return result
        }
        return null
    }
    
}