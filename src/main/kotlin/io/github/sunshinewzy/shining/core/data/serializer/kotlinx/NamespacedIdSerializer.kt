package io.github.sunshinewzy.shining.core.data.serializer.kotlinx

import io.github.sunshinewzy.shining.api.namespace.NamespacedId

object NamespacedIdSerializer : StringSerializer<NamespacedId>("NamespacedId") {
    override fun toString(value: NamespacedId): String {
        return value.toString()
    }

    override fun fromString(source: String): NamespacedId {
        return NamespacedId.fromString(source) ?: NamespacedId.NULL
    }
}