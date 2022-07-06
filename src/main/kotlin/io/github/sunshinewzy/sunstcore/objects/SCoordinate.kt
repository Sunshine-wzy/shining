package io.github.sunshinewzy.sunstcore.objects

import org.bukkit.util.NumberConversions

data class SCoordinate(var x: Int, var y: Int, var z: Int) {

    constructor(y: Int) : this(0, y, 0)
    
    constructor(str: String) : this(0, 0, 0) {
        val coord = str.split(",")
        if(coord.size == 3){
            this.x = NumberConversions.toInt(coord[0])
            this.y = NumberConversions.toInt(coord[1])
            this.z = NumberConversions.toInt(coord[2])
        } else throw formatException
    }
    
    override fun toString(): String = "$x,$y,$z"
    
    
    companion object {
        private val formatException = IllegalArgumentException("The format of SCoordinate must be 'x,y,z'.")
    }
    
}

data class SFlatCoord(var x: Int, var y: Int)
