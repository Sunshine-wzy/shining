package io.github.sunshinewzy.shining.api.objects.coordinate

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class Coordinate3D(val x: Int, val y: Int, val z: Int) {
    
    operator fun plus(coordinate: Coordinate3D): Coordinate3D =
        Coordinate3D(x + coordinate.x, y + coordinate.y, z + coordinate.z)
    
    operator fun minus(coordinate: Coordinate3D): Coordinate3D =
        Coordinate3D(x - coordinate.x, y - coordinate.y, z - coordinate.z)
    
    fun add(x: Int, y: Int, z: Int): Coordinate3D =
        Coordinate3D(this.x + x, this.y + y, this.z + z)

    @JsonValue
    override fun toString(): String {
        return "$x,$y,$z"
    }


    companion object {
        
        @JvmField
        val ORIGIN = Coordinate3D(0, 0, 0)

        @JvmStatic
        @JsonCreator
        fun fromString(source: String): Coordinate3D? =
            CoordinateUtils.coordinate3DFromString(source)

    }
    
}