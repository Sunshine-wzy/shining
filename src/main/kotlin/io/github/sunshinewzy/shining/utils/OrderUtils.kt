package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.objects.SFlatCoord
import io.github.sunshinewzy.shining.objects.coordinate.Coordinate2D

object OrderUtils {
    
    @JvmStatic
    @JvmOverloads
    fun getFullOrders(endLine: Int, startLine: Int = 1): MutableList<Int> {
        val list = ArrayList<Int>()
        for (i in startLine..endLine) {
            val startOrder = 1 orderWith i
            for (j in 0 until 9) {
                list += startOrder + j
            }
        }
        return list
    }
    
}


/**
 * x, y坐标 均从1开始
 * order 从0开始
 */
infix fun Int.orderWith(y: Int): Int = (y - 1) * 9 + (this - 1)


fun Int.toCoordinate2D(): Coordinate2D = Coordinate2D(this % 9 + 1, this / 9 + 1)

fun Int.toFlatCoord(): SFlatCoord = SFlatCoord(this % 9 + 1, this / 9 + 1)


fun Int.toX(length: Int): Int = this % length + 1

fun Int.toY(length: Int): Int = this / length + 1


fun Triple<Int, Int, Int>.add(x: Int, y: Int, z: Int): Triple<Int, Int, Int> =
    Triple(first + x, second + y, third + z)
