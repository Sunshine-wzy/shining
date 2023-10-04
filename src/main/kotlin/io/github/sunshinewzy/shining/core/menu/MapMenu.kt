package io.github.sunshinewzy.shining.core.menu

import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate2D
import io.github.sunshinewzy.shining.api.objects.coordinate.Rectangle
import io.github.sunshinewzy.shining.utils.orderWith
import io.github.sunshinewzy.shining.utils.toCoordinate2D
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir
import java.util.concurrent.ConcurrentHashMap

open class MapMenu<T>(title: String) : Basic(title) {
    
    var offset: Coordinate2D = Coordinate2D.ORIGIN
        private set
    var baseCoordinate: Coordinate2D = Coordinate2D(3, 3)
        private set
    var moveSpeed: Int = 2
        private set
    var isShinyMoveItem: Boolean = true
        private set
    internal var menuLocked: Boolean = true
    internal var menuArea: Rectangle = Rectangle.ORIGIN
    internal var offsetArea: Rectangle = Rectangle.ORIGIN
    internal var elementsCallback: () -> Map<Coordinate2D, T> = { ConcurrentHashMap() }
    internal var elementsCache: Map<Coordinate2D, T> = emptyMap()
    internal var elementClickCallback: ((event: ClickEvent, element: T, coordinate: Coordinate2D) -> Unit) = { _, _, _ -> }
    internal var clickEmptyCallback: ((event: ClickEvent, coordinate: Coordinate2D) -> Unit) = { _, _ -> }
    internal var generateCallback: ((player: Player, element: T, coordinate: Coordinate2D, slot: Int) -> ItemStack) = { _, _, _, _ -> ItemStack(Material.AIR) }
    internal var asyncGenerateCallback: ((player: Player, element: T, coordinate: Coordinate2D, slot: Int) -> ItemStack) = { _, _, _, _ -> ItemStack(Material.AIR) }
    
    private lateinit var player: Player
    private val moveItems: Array<Pair<Int, (offset: Coordinate2D) -> ItemStack>?> = Array(4) { null }
    
    
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
    
    open fun base(base: Coordinate2D) {
        baseCoordinate = base
    }
    
    open fun offset(offset: Coordinate2D) {
        this.offset = offset
        updateOffsetArea()
    }
    
    open fun shinyMoveItem(shinyMoveItem: Boolean) {
        isShinyMoveItem = shinyMoveItem
    }
    
    open fun elements(elements: () -> Map<Coordinate2D, T>) {
        elementsCallback = elements
    }
    
    open fun onGenerate(async: Boolean = false, callback: (player: Player, element: T, coordinate: Coordinate2D, slot: Int) -> ItemStack) {
        if (async) asyncGenerateCallback = callback
        else generateCallback = callback
    }
    
    open fun onClick(callback: (event: ClickEvent, element: T, coordinate: Coordinate2D) -> Unit) {
        elementClickCallback = callback
    }
    
    open fun onClickEmpty(callback: (event: ClickEvent, coordinate: Coordinate2D) -> Unit) {
        clickEmptyCallback = callback
    }
    
    open fun setMoveRight(slot: Int, callback: (offset: Coordinate2D) -> ItemStack) {
        moveItems[Direction.RIGHT.ordinal] = slot to callback
        onClick(slot) { 
            offset = offset.add(moveSpeed, 0)
            updateOffsetArea()
            player.openInventory(build())
        }
    }
    
    open fun setMoveLeft(slot: Int, callback: (offset: Coordinate2D) -> ItemStack) {
        moveItems[Direction.LEFT.ordinal] = slot to callback
        onClick(slot) {
            offset = offset.add(-moveSpeed, 0)
            updateOffsetArea()
            player.openInventory(build())
        }
    }

    open fun setMoveDown(slot: Int, callback: (offset: Coordinate2D) -> ItemStack) {
        moveItems[Direction.DOWN.ordinal] = slot to callback
        onClick(slot) {
            offset = offset.add(0, moveSpeed)
            updateOffsetArea()
            player.openInventory(build())
        }
    }

    open fun setMoveUp(slot: Int, callback: (offset: Coordinate2D) -> ItemStack) {
        moveItems[Direction.UP.ordinal] = slot to callback
        onClick(slot) {
            offset = offset.add(0, -moveSpeed)
            updateOffsetArea()
            player.openInventory(build())
        }
    }
    
    open fun setMoveToOrigin(slot: Int, callback: () -> ItemStack) {
        set(slot, callback)
        onClick(slot) {
            offset(Coordinate2D.ORIGIN)
            player.openInventory(build())
        }
    }
    
    
    override fun build(): Inventory {
        elementsCache = elementsCallback()
        val elementMap = HashMap<Int, Pair<T, Coordinate2D>>()
        
        fun processBuild(player: Player, inventory: Inventory, async: Boolean) {
            this.player = player
            val flagShiny = BooleanArray(4) { false }
            elementsCache.forEach { (coordinate, element) -> 
                val thePoint = baseCoordinate + coordinate
                if (thePoint in offsetArea) {
                    val slot = (thePoint.x - offset.x) orderWith (thePoint.y - offset.y)
                    elementMap[slot] = element to coordinate
                    val callback = if (async) asyncGenerateCallback else generateCallback
                    val item = callback(player, element, coordinate, slot)
                    if (item.isNotAir()) {
                        inventory.setItem(slot, item)
                    }
                } else if (isShinyMoveItem) {
                    if (thePoint.x < offsetArea.first.x) {
                        flagShiny[Direction.LEFT.ordinal] = true
                    } else if (thePoint.x > offsetArea.second.x) {
                        flagShiny[Direction.RIGHT.ordinal] = true
                    }
                    
                    if (thePoint.y < offsetArea.first.y) {
                        flagShiny[Direction.UP.ordinal] = true
                    } else if (thePoint.y > offsetArea.second.y) {
                        flagShiny[Direction.DOWN.ordinal] = true
                    }
                }
            }
            
            for (i in 0 until 4) {
                val (slot, callback) = moveItems[i] ?: continue
                val itemStack = if (flagShiny[i]) buildItem(callback(offset)) { shiny() } else callback(offset)
                inventory.setItem(slot, itemStack)
            }
        }
        
        selfBuild { player, inventory -> processBuild(player, inventory, false) }
        selfBuild(async = true) { player, inventory -> processBuild(player, inventory, true) }
        selfClick { 
            if (menuLocked) {
                it.isCancelled = true
            }
            elementMap[it.rawSlot]?.let { pair ->
                elementClickCallback(it, pair.first, pair.second)
            } ?: kotlin.run { 
                val rawCoordinate = it.rawSlot.toCoordinate2D()
                if (rawCoordinate in menuArea && it.currentItem.isAir()) {
                    clickEmptyCallback(it, rawCoordinate + offset - baseCoordinate)
                }
            }
        }
        return super.build()
    }
    
    
    private fun updateOffsetArea() {
        offsetArea = menuArea + offset
    }
    
    
    enum class Direction {
        UP, DOWN, LEFT, RIGHT;
    }
    
}