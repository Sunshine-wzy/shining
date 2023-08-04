package io.github.sunshinewzy.shining.objects

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class Coordinate2D(val x: Int, val y: Int) {

    fun isOrigin(): Boolean = x == 0 && y == 0
    
    @JsonValue
    override fun toString(): String {
        return "$x,$y"
    }
    
    
    companion object {
        
        @JvmStatic
        val ORIGIN: Coordinate2D = Coordinate2D(0, 0)
        
        
        @JvmStatic
        @JsonCreator
        fun fromString(source: String): Coordinate2D? {
            val list = source.split(",")
            if (list.size != 2) return null
            val x = list[0].toIntOrNull() ?: return null
            val y = list[1].toIntOrNull() ?: return null
            return Coordinate2D(x, y)
        }
        
    }
    
}