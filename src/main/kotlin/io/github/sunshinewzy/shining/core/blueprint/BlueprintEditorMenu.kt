package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintClass
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNode
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNodeTree
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate2D
import io.github.sunshinewzy.shining.core.menu.MapMenu
import io.github.sunshinewzy.shining.utils.orderWith
import io.github.sunshinewzy.shining.utils.toCoordinate2D
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.util.subList
import taboolib.platform.util.isAir

open class BlueprintEditorMenu(title: String) : MapMenu<IBlueprintNode>(title) {
    
    var blueprintClass: IBlueprintClass = BlueprintClass()
        private set
    var nodeTreePage: Int = 0
        private set
    
    protected var nodeTreePageChangeCallback: ((player: Player) -> Unit) = { _ -> }
    
    
    open fun blueprint(blueprint: IBlueprintClass) {
        this.blueprintClass = blueprint
    }

    open fun setNodeTreePreviousPage(slot: Int, callback: (page: Int, hasPreviousPage: Boolean) -> ItemStack) {
        set(slot) { callback(nodeTreePage, hasNodeTreePreviousPage()) }
        onClick(slot) {
            if (hasNodeTreePreviousPage()) {
                nodeTreePage--
                player.openInventory(build())
                nodeTreePageChangeCallback(player)
            }
        }
    }
    
    open fun setNodeTreeNextPage(slot: Int, callback: (page: Int, hasNextPage: Boolean) -> ItemStack) {
        set(slot) { callback(nodeTreePage, hasNodeTreeNextPage()) }
        onClick(slot) {
            if (hasNodeTreeNextPage()) {
                nodeTreePage++
                player.openInventory(build())
                nodeTreePageChangeCallback(player)
            }
        }
    }
    
    open fun hasNodeTreePreviousPage(): Boolean = nodeTreePage > 0
    
    open fun hasNodeTreeNextPage(): Boolean =
        blueprintClass.getNodeTrees().size / 5f > nodeTreePage + 1

    open fun onNodeTreePageChange(callback: (player: Player) -> Unit) {
        nodeTreePageChangeCallback = callback
    }
    
    open fun switchToNodeTree(tree: IBlueprintNodeTree) {
        val map = HashMap<Coordinate2D, IBlueprintNode>()
        tree.getRootOrNull()?.let { root ->
            
        }
        elementsCache = map
    }
    
    protected val nodeTreeElementMap = HashMap<Int, IBlueprintNodeTree>()
    protected var nodeTreeElementItems: List<IBlueprintNodeTree> = emptyList()

    override fun processBuild(player: Player, inventory: Inventory, async: Boolean) {
        super.processBuild(player, inventory, async)
        nodeTreeElementItems.forEachIndexed { index, tree ->
            val slot = NODE_TREE_BASE_INDEX + index
            nodeTreeElementMap[slot] = tree
            inventory.setItem(slot, tree.getRootOrNull()?.getIcon() ?: ItemStack(Material.GLASS))
        }
    }

    override fun processSelfBuild() {
        elementsCache = elementsCallback()
        val elementMap = HashMap<Int, Pair<IBlueprintNode, Coordinate2D>>()
        nodeTreeElementMap.clear()
        nodeTreeElementItems = subList(blueprintClass.getNodeTrees(), nodeTreePage * 5, (nodeTreePage + 1) * 5)

        selfBuild { player, inventory -> processBuild(player, inventory, false) }
        selfBuild(async = true) { player, inventory -> processBuild(player, inventory, true) }
        selfClick {
            if (menuLocked) {
                it.isCancelled = true
            }
            elementMap[it.rawSlot]?.let { pair ->
                elementClickCallback(it, pair.first, pair.second)
            } ?: nodeTreeElementMap[it.rawSlot]?.let { tree ->
                switchToNodeTree(tree)
            } ?: kotlin.run {
                val rawCoordinate = it.rawSlot.toCoordinate2D()
                if (rawCoordinate in menuArea && it.currentItem.isAir()) {
                    clickEmptyCallback(it, rawCoordinate + offset - baseCoordinate)
                }
            }
        }
    }

    
    companion object {
        val NODE_TREE_BASE_INDEX = 3 orderWith 6
    }
    
}