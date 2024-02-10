package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.BlueprintNodeType
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintClass
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintEditor
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNode
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.core.guide.context.AbstractGuideContextElement
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.menu.PageableCategoryChest
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.platform.util.buildItem

object BlueprintEditor : IBlueprintEditor {

    override fun open(player: Player, blueprint: IBlueprintClass?) {
        if (blueprint == null) {
            val bp = BlueprintClass()
            bp.edit(player)
        } else {
            blueprint.edit(player)
        }
    }

    override fun openNodeSelector(player: Player, defaultType: BlueprintNodeType, context: GuideContext) {
        player.openMenu<PageableCategoryChest<BlueprintNodeType, IBlueprintNode>>(player.getLangText("menu-editor-blueprint-node_selector-title")) { 
            rows(6)
            map(
                "-B-------",
                "*********",
                "*********",
                "*********",
                "p*******n",
                "P-@@@@@-N"
            )
            
            slotsBy('*')
            categorySlotsBy('@')
            set('-', ShiningIcon.EDGE.item)
            context[GuideEditorContext.Back]?.let { 
                onBack(player) { it.onBack(this) }
            }
            
            currentCategory(defaultType)
            categoryElements { BlueprintNodeType.values().toList() }
            elements { BlueprintNodeRegistry.get(it) }
            
            onGenerate { player, element, _, _ -> 
                buildItem(element.icon) {
                    name = element.getName(player)
                    lore += element.getDescription(player)
                    colored()
                }
            }
            onCategoryGenerate { player, element, _, _ -> 
                BlueprintNodeRegistry.getTypeIcon(element).toLocalizedItemStack(player)
            }
            
            setPreviousPage(getFirstSlot('p')) { _, hasPreviousPage ->
                if (hasPreviousPage) ShiningIcon.PAGE_PREVIOUS_GLASS_PANE.toLocalizedItem(player)
                else ShiningIcon.EDGE.item
            }
            setNextPage(getFirstSlot('n')) { _, hasNextPage ->
                if (hasNextPage) ShiningIcon.PAGE_NEXT_GLASS_PANE.toLocalizedItem(player)
                else ShiningIcon.EDGE.item
            }
            
            setCategoryPreviousPage(getFirstSlot('P')) { _, hasPreviousPage ->
                if (hasPreviousPage) {
                    buildItem(ShiningIcon.PAGE_PREVIOUS_GLASS_PANE.toLocalizedItem(player)) {
                        material = Material.YELLOW_STAINED_GLASS_PANE
                    }
                } else ShiningIcon.EDGE.item
            }
            setCategoryNextPage(getFirstSlot('N')) { _, hasNextPage ->
                if (hasNextPage) {
                    buildItem(ShiningIcon.PAGE_NEXT_GLASS_PANE.toLocalizedItem(player)) {
                        material = Material.YELLOW_STAINED_GLASS_PANE
                    }
                } else ShiningIcon.EDGE.item
            }
            
            onClick { _, element ->
                context[SelectNodeContext]?.let { 
                    it.onSelectNode(element.instantiate())
                }
            }
        }
    }
    
    
    class SelectNodeContext(val onSelectNode: (IBlueprintNode) -> Unit) : AbstractGuideContextElement(SelectNodeContext) {
        companion object : GuideContext.Key<SelectNodeContext>
    }
    
}