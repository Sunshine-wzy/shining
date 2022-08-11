package io.github.sunshinewzy.sunstcore.core.data.serializer

import io.github.sunshinewzy.sunstcore.api.NamespacedKey

object NamespacedKeySerializer : StringSerializer<NamespacedKey>("NamespacedKey") {
    override fun toString(value: NamespacedKey): String {
        return value.toString()
    }

    override fun fromString(source: String): NamespacedKey {
        return NamespacedKey.fromString(source) ?: NamespacedKey.NULL
    }
}