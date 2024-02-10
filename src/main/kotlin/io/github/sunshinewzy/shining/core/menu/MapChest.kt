package io.github.sunshinewzy.shining.core.menu

import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate2D
import io.github.sunshinewzy.shining.api.objects.coordinate.Rectangle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.type.Chest

interface MapChest<T> : Chest {
    
    val offset: Coordinate2D
    
    val baseCoordinate: Coordinate2D
    
    val moveSpeed: Int
    
    val isShinyMoveItem: Boolean
    

    fun speed(speed: Int)

    fun menuLocked(lockAll: Boolean)

    fun area(area: Rectangle)

    fun base(base: Coordinate2D)

    fun offset(offset: Coordinate2D)

    fun shinyMoveItem(shinyMoveItem: Boolean)

    fun elements(elements: () -> Map<Coordinate2D, T>)

    fun onGenerate(async: Boolean = false, callback: (player: Player, element: T, coordinate: Coordinate2D, slot: Int) -> ItemStack)

    fun onClick(callback: (event: ClickEvent, element: T, coordinate: Coordinate2D) -> Unit)

    fun onClickEmpty(callback: (event: ClickEvent, coordinate: Coordinate2D) -> Unit)

    fun onMove(callback: (player: Player) -> Unit)

    fun setMoveRight(slot: Int, callback: (offset: Coordinate2D) -> ItemStack)

    fun setMoveLeft(slot: Int, callback: (offset: Coordinate2D) -> ItemStack)

    fun setMoveDown(slot: Int, callback: (offset: Coordinate2D) -> ItemStack)

    fun setMoveUp(slot: Int, callback: (offset: Coordinate2D) -> ItemStack)

    fun setMoveToOrigin(slot: Int, callback: () -> ItemStack)
    
}