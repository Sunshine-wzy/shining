package io.github.sunshinewzy.sunstcore.core.data.serializer

import io.github.sunshinewzy.sunstcore.api.NamespacedId

object NamespacedIdSerializer : StringSerializer<NamespacedId>("NamespacedId") {
    override fun toString(value: NamespacedId): String {
        return value.toString()
    }

    override fun fromString(source: String): NamespacedId {
        return NamespacedId.fromString(source) ?: NamespacedId.NULL
    }
}