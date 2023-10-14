package io.github.sunshinewzy.shining.api.objects.coordinate

data class MutableCoordinate3D(var x: Int = 0, var y: Int = 0, var z: Int = 0) {
    
    fun setCoordinate(x: Int, y: Int, z: Int): MutableCoordinate3D {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    override fun toString(): String {
        return "$x,$y,$z"
    }
    
}
