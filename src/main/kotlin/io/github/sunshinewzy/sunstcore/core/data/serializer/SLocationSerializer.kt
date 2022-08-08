package io.github.sunshinewzy.sunstcore.core.data.serializer

import io.github.sunshinewzy.sunstcore.objects.SLocation

object SLocationSerializer : StringSerializer<SLocation>("SLocation") {
    override fun toString(value: SLocation): String {
        return value.toString()
    }

    override fun fromString(source: String): SLocation {
        return SLocation(source)
    }
}