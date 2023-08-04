package io.github.sunshinewzy.shining.core.menu

import io.github.sunshinewzy.shining.objects.coordinate.Coordinate2D
import io.github.sunshinewzy.shining.objects.coordinate.Rectangle
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.type.Basic
import taboolib.platform.util.isNotAir
import java.util.concurrent.ConcurrentHashMap

open class MapMenu<T>(title: String) : Basic(title) {
    
    var offset: Coordinate2D = Coordinate2D.ORIGIN
        private set
    var basePoint: Coordinate2D = Coordinate2D(2, 2)
        private set
    var moveSpeed: Int = 3
        private set
    internal var menuLocked: Boolean = true
    internal var menuArea: Rectangle = Rectangle.ORIGIN
    internal var offsetArea: Rectangle = Rectangle.ORIGIN
    internal var elementsCallback: () -> Map<Coordinate2D, T> = { ConcurrentHashMap() }
    internal var elementsCache: Map<Coordinate2D, T> = emptyMap()
    internal var elementClickCallback: ((event: ClickEvent, element: T) -> Unit) = { _, _ -> }
    internal var generateCallback: ((player: Player, element: T, coordinate: Coordinate2D, slot: Int) -> ItemStack) = { _, _, _, _ -> ItemStack(Material.AIR) }
    internal var asyncGenerateCallback: ((player: Player, element: T, coordinate: Coordinate2D, slot: Int) -> ItemStack) = { _, _, _, _ -> ItemStack(Material.AIR) }
    
    private lateinit var player: Player
    
    
    open fun speed(speed: Int) {
        moveSpeed = speed
    }
    
    open fun menuLocked(lockAll: Boolean) {
        this.menuLocked = lockAll
    }
    
    open fun area(area: Rectangle) {
        this.menuArea = area
        updateOffsetArea()
    }
    
    open fun elements(elements: () -> Map<Coordinate2D, T>) {
        elementsCallback = elements
    }
    
    open fun onGenerate(async: Boolean = false, callback: (player: Player, element: T, coordinate: Coordinate2D, slot: Int) -> ItemStack) {
        if (async) asyncGenerateCallback = callback
        else generateCallback = callback
    }
    
    open fun onClick(callback: (event: ClickEvent, element: T) -> Unit) {
        elementClickCallback = callback
    }
    
    open fun setMoveRight(slot: Int, callback: (offset: Coordinate2D) -> ItemStack) {
        set(slot) { callback(offset) }
        onClick(slot) { 
            offset = offset.add(moveSpeed, 0)
            updateOffsetArea()
            player.openInventory(build())
        }
    }
    
    open fun setMoveLeft(slot: Int, callback: (offset: Coordinate2D) -> ItemStack) {
        set(slot) { callback(offset) }
        onClick(slot) {
            offset = offset.add(-moveSpeed, 0)
            updateOffsetArea()
            player.openInventory(build())
        }
    }

    open fun setMoveDown(slot: Int, callback: (offset: Coordinate2D) -> ItemStack) {
        set(slot) { callback(offset) }
        onClick(slot) {
            offset = offset.add(0, moveSpeed)
            updateOffsetArea()
            player.openInventory(build())
        }
    }

    open fun setMoveUp(slot: Int, callback: (offset: Coordinate2D) -> ItemStack) {
        set(slot) { callback(offset) }
        onClick(slot) {
            offset = offset.add(0, -moveSpeed)
            updateOffsetArea()
            player.openInventory(build())
        }
    }
    
    
    override fun build(): Inventory {
        elementsCache = elementsCallback()
        val elementMap = HashMap<Int, T>()
        
        fun processBuild(player: Player, inventory: Inventory, async: Boolean) {
            this.player = player
            elementsCache.forEach { (coordinate, element) -> 
                val thePoint = basePoint + coordinate
                if (thePoint in offsetArea) {
                    val slot = (thePoint.x - offsetArea.first.x + menuArea.first.x) orderWith (thePoint.y - offsetArea.first.y + menuArea.first.y)
                    elementMap[slot] = element
                    val callback = if (async) asyncGenerateCallback else generateCallback
                    val item = callback(player, element, coordinate, slot)
                    if (item.isNotAir()) {
                        inventory.setItem(slot, item)
                    }
                }
            }
        }
        
        selfBuild { player, inventory -> processBuild(player, inventory, false) }
        selfBuild(async = true) { player, inventory -> processBuild(player, inventory, true) }
        selfClick { 
            if (menuLocked) {
                it.isCancelled = true
            }
            elementClickCallback(it, elementMap[it.rawSlot] ?: return@selfClick)
        }
        return super.build()
    }
    
    
    private fun updateOffsetArea() {
        offsetArea = menuArea + offset
    }
    
}