package io.github.sunshinewzy.shining.core.data.serializer.kotlinx

import io.github.sunshinewzy.shining.objects.SLocation

object SLocationSerializer : StringSerializer<SLocation>("SLocation") {
    override fun toString(value: SLocation): String {
        return value.toString()
    }

    override fun fromString(source: String): SLocation {
        return SLocation(source)
    }
}