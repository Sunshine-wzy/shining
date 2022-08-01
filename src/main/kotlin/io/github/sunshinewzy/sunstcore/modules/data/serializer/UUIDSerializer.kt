package io.github.sunshinewzy.sunstcore.modules.data.serializer

import java.util.*

object UUIDSerializer : StringSerializer<UUID>("UUID") {

    override fun toString(value: UUID): String =
        value.toString()

    override fun fromString(source: String): UUID =
        UUID.fromString(source)
    
}