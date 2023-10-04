package io.github.sunshinewzy.shining.api.objects.coordinate

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
        fun fromString(source: String): Coordinate3D? =
            CoordinateUtils.coordinate3DFromString(source)

    }
    
}