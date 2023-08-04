package io.github.sunshinewzy.shining.objects.coordinate

data class Rectangle(val first: Coordinate2D, val second: Coordinate2D) {
    
    constructor(x1: Int, y1: Int, x2: Int, y2: Int) : this(Coordinate2D(x1, y1), Coordinate2D(x2, y2))
    
    
    operator fun plus(coordinate: Coordinate2D): Rectangle =
        Rectangle(first + coordinate, second + coordinate)
    
    operator fun minus(coordinate: Coordinate2D): Rectangle =
        Rectangle(first - coordinate, second - coordinate)
    
    operator fun contains(coordinate: Coordinate2D): Boolean =
        coordinate.x in first.x..second.x && coordinate.y in first.y..second.y
    
    
    companion object {
        @JvmStatic
        val ORIGIN: Rectangle = Rectangle(Coordinate2D.ORIGIN, Coordinate2D.ORIGIN)
    }
    
}