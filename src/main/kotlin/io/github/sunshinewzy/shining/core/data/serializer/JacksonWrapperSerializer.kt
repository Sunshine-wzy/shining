package io.github.sunshinewzy.shining.core.data.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.github.sunshinewzy.shining.core.data.JacksonWrapper

object JacksonWrapperSerializer : StdSerializer<JacksonWrapper<*>>(JacksonWrapper::class.java) {
    
    private fun readResolve(): Any = JacksonWrapperSerializer

    override fun serialize(value: JacksonWrapper<*>, gen: JsonGenerator, provider: SerializerProvider) {
        provider.defaultSerializeValue(value.value, gen)
    }
    
}