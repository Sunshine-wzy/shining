package io.github.sunshinewzy.shining.core.data

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.TypeFactory

class JacksonWrapper<T>(@JsonValue val value: T) {
    
    companion object {
        @JvmStatic
        fun <V> type(type: Class<V>): JavaType =
            TypeFactory.defaultInstance()
                .constructParametricType(JacksonWrapper::class.java, type)
    }
    
}