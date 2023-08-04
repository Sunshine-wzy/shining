package io.github.sunshinewzy.shining.objects

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class Coordinate3D(val x: Int, val y: Int, val z: Int) {

    @JsonValue
    override fun toString(): String {
        return "$x,$y,$z"
    }


    companion object {

        @JvmStatic
        @JsonCreator
        fun fromString(source: String): Coordinate3D? {
            val list = source.split(",")
            if (list.size != 3) return null
            val x = list[0].toIntOrNull() ?: return null
            val y = list[1].toIntOrNull() ?: return null
            val z = list[2].toIntOrNull() ?: return null
            return Coordinate3D(x, y, z)
        }

    }
    
}