package io.github.sunshinewzy.shining.objects

import org.bukkit.util.NumberConversions

data class SCoordinate(var x: Int, var y: Int, var z: Int) {

    constructor(y: Int) : this(0, y, 0)
    
    constructor(source: String) : this(0, 0, 0) {
        val pos = source.split(",")
        if(pos.size == 3) {
            this.x = NumberConversions.toInt(pos[0])
            this.y = NumberConversions.toInt(pos[1])
            this.z = NumberConversions.toInt(pos[2])
        } else throw formatException
    }
    
    override fun toString(): String = "$x,$y,$z"
    
    
    companion object {
        private val formatException = IllegalArgumentException("The format of SPosition must be 'x,y,z'.")
    }
    
}

data class SFlatCoord(var x: Int, var y: Int)
