package io.github.sunshinewzy.shining.objects

/**
 * x, y坐标 均从1开始
 * order 从0开始
 */
infix fun Int.orderWith(y: Int): Int = (y - 1) * 9 + (this - 1)


fun Int.toCoordinate(): SFlatCoord = SFlatCoord(this%9 + 1, this/9 + 1)


fun Int.toX(length: Int): Int = this % length + 1

fun Int.toY(length: Int): Int = this / length + 1


fun Triple<Int, Int, Int>.add(x: Int, y: Int, z: Int): Triple<Int, Int, Int> =
    Triple(first + x, second + y, third + z)