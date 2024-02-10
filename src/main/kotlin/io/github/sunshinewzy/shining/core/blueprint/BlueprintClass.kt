package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintClass
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNodeTree
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate2D
import io.github.sunshinewzy.shining.api.objects.coordinate.Rectangle
import io.github.sunshinewzy.shining.core.guide.element.GuideMap
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.menu.onBuildEdge
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.platform.util.buildItem

class BlueprintClass : IBlueprintClass {
    
    private val nodeTrees: ArrayList<IBlueprintNodeTree> = ArrayList()
    

    override fun getNodeTrees(): ArrayList<IBlueprintNodeTree> = nodeTrees

    override fun edit(player: Player) {
        player.openMenu<BlueprintEditorChest>(player.getLangText("menu-editor-blueprint-title")) { 
            rows(6)
            area(Rectangle(2, 2, 8, 5))
            base(Coordinate2D(2, 2))
            blueprint(this@BlueprintClass)

            onBuildEdge(GuideMap.edgeOrders)

            setMoveRight(9 orderWith 3) { ShiningIcon.MOVE_RIGHT.toLocalizedItem(player) }
            setMoveLeft(1 orderWith 3) { ShiningIcon.MOVE_LEFT.toLocalizedItem(player) }
            setMoveUp(1 orderWith 4) { ShiningIcon.MOVE_UP.toLocalizedItem(player) }
            setMoveDown(9 orderWith 4) { ShiningIcon.MOVE_DOWN.toLocalizedItem(player) }
            setMoveToOrigin(8 orderWith 1) { ShiningIcon.MOVE_TO_ORIGIN.toLocalizedItem(player) }
            
            setNodeTreePreviousPage(1 orderWith 6) { _, hasPreviousPage ->
                if (hasPreviousPage) {
                    buildItem(ShiningIcon.PAGE_PREVIOUS_GLASS_PANE.toLocalizedItem(player)) {
                        material = Material.YELLOW_STAINED_GLASS_PANE
                    }
                } else ShiningIcon.EDGE.item
            }
            setNodeTreeNextPage(9 orderWith 6) { _, hasNextPage ->
                if (hasNextPage) {
                    buildItem(ShiningIcon.PAGE_NEXT_GLASS_PANE.toLocalizedItem(player)) {
                        material = Material.YELLOW_STAINED_GLASS_PANE
                    }
                } else ShiningIcon.EDGE.item
            }
        }
    }
    
}