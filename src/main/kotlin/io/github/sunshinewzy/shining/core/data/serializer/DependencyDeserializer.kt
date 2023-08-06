package io.github.sunshinewzy.shining.core.data.serializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import taboolib.common.env.Dependency
import taboolib.common.env.DependencyScope

object DependencyDeserializer : StdDeserializer<Dependency>(Dependency::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Dependency {
        val args = p.valueAsString.split(":")
        return Dependency(args[0], args[1], args[2], DependencyScope.RUNTIME)
    }
    
}