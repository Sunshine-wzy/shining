package io.github.sunshinewzy.sunstcore.modules.machine

import io.github.sunshinewzy.sunstcore.objects.SLocation.Companion.toSLocation
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * 机器运行事件
 * 当机器运行时触发
 * 
 * @param loc 触发位置
 */
sealed class SMachineRunEvent(val loc: Location) {
    val sLoc = loc.toSLocation()

    /**
     * 手动机器运行事件
     * 
     * @param loc 玩家触发机器点击的方块位置
     */
    class Manual(loc: Location, val player: Player) : SMachineRunEvent(loc)

    /**
     * 自动机器运行事件
     * 
     * @param loc 机器中心位置 centerLoc - 即机器的基础位置坐标加中心相对位置坐标
     * @see SMachineStructure.center
     */
    class Timer(loc: Location) : SMachineRunEvent(loc)

}
