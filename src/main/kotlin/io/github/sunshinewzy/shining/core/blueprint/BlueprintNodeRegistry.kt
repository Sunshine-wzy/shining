package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.blueprint.BlueprintNodeType
import io.github.sunshinewzy.shining.api.blueprint.BlueprintNodeType.FLOW_CONTROL
import io.github.sunshinewzy.shining.api.blueprint.BlueprintNodeType.FUNCTION
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNode
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNodeRegistry
import io.github.sunshinewzy.shining.api.lang.item.ILanguageItem
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.blueprint.node.BranchNode
import io.github.sunshinewzy.shining.core.blueprint.node.BreakBlockNode
import io.github.sunshinewzy.shining.core.blueprint.node.PlaceBlockNode
import io.github.sunshinewzy.shining.core.blueprint.node.SpawnItemNode
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo
import java.util.*

@SkipTo(LifeCycle.ENABLE)
object BlueprintNodeRegistry : IBlueprintNodeRegistry {

    private val typeIconMap: MutableMap<BlueprintNodeType, ILanguageItem> = EnumMap(BlueprintNodeType::class.java)
    private val nodeMap: MutableMap<BlueprintNodeType, MutableList<IBlueprintNode>> = EnumMap(BlueprintNodeType::class.java)

    init {
        BlueprintNodeType.values().forEach { type ->
            typeIconMap[type] = NamespacedIdItem(type.material, NamespacedId(Shining, type.languageItemId))
        }
        
        register(FUNCTION, BreakBlockNode())
        register(FUNCTION, PlaceBlockNode())
        register(FUNCTION, SpawnItemNode())
        
        register(FLOW_CONTROL, BranchNode())
    }


    override fun get(type: BlueprintNodeType): List<IBlueprintNode> =
        nodeMap.getOrPut(type) { ArrayList() }

    override fun register(type: BlueprintNodeType, node: IBlueprintNode) {
        nodeMap.getOrPut(type) { ArrayList() }.add(node)
    }

    override fun getTypeIcon(type: BlueprintNodeType): ILanguageItem = typeIconMap[type]!!
    
}