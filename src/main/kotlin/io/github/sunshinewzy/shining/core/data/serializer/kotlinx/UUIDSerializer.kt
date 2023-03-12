package io.github.sunshinewzy.shining.core.data.serializer.kotlinx

import java.util.*

object UUIDSerializer : StringSerializer<UUID>("UUID") {

    override fun toString(value: UUID): String =
        value.toString()

    override fun fromString(source: String): UUID =
        UUID.fromString(source)

}