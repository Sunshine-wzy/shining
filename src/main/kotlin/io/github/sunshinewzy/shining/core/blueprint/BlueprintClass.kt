package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.BlueprintComponentLifecycle
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintClass
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintComponent
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
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class BlueprintClass : IBlueprintClass {
    
    private val nodeTrees: ArrayList<IBlueprintNodeTree> = ArrayList()
    private val componentRegistry: MutableMap<Class<out IBlueprintComponent>, IBlueprintComponent> = ConcurrentHashMap()
    private val componentLifecycles: MutableMap<Class<out IBlueprintComponent>, MutableSet<BlueprintComponentLifecycle>> = HashMap()
    private val componentLifecycleRegistry: MutableMap<BlueprintComponentLifecycle, MutableSet<Class<out IBlueprintComponent>>> = EnumMap(BlueprintComponentLifecycle::class.java)


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

    override fun <T : IBlueprintComponent> getComponent(type: Class<T>): T =
        getComponentOrNull(type) ?: throw IllegalArgumentException("The blueprint does not have a component of type '$type'.")

    @Suppress("UNCHECKED_CAST")
    override fun <T : IBlueprintComponent> getComponentOrNull(type: Class<T>): T? =
        componentRegistry[type] as? T

    override fun <T : IBlueprintComponent> addComponent(type: Class<T>, component: T): T {
        componentRegistry[type] = component

        val lifecycles = scanComponentLifecycleMethods(type)
        componentLifecycles[type] = lifecycles

        lifecycles.forEach { lifecycle ->
            componentLifecycleRegistry
                .getOrPut(lifecycle) { HashSet() }
                .add(type)
        }

        if (hasComponentLifecycle(type, BlueprintComponentLifecycle.LOAD))
            component.onLoad()
        return component
    }

    override fun <T : IBlueprintComponent> addComponent(type: Class<T>): T {
        val component = type.getConstructor(IBlueprintClass::class.java).newInstance(this) as T
        return addComponent(type, component)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : IBlueprintComponent> removeComponent(type: Class<T>): T? {
        val component = componentRegistry.remove(type) as? T ?: return null
        if (hasComponentLifecycle(type, BlueprintComponentLifecycle.DESTROY))
            component.onDestroy()
        componentLifecycles.remove(type)?.forEach { lifecycle ->
            componentLifecycleRegistry[lifecycle]?.remove(type)
        }
        return component
    }

    override fun <T : IBlueprintComponent> hasComponent(type: Class<T>): Boolean =
        componentRegistry.containsKey(type)

    override fun <T : IBlueprintComponent?> hasComponentLifecycle(type: Class<T>, lifecycle: BlueprintComponentLifecycle): Boolean {
        val lifecycles = componentLifecycles[type] ?: return false
        return lifecycles.contains(lifecycle)
    }

    override fun doLifecycle(lifecycle: BlueprintComponentLifecycle) {
        componentLifecycleRegistry[lifecycle]?.forEach { type ->
            val component = getComponent(type)
            when (lifecycle) {
                BlueprintComponentLifecycle.LOAD -> component.onLoad()
                BlueprintComponentLifecycle.ENABLE -> component.onEnable()
                BlueprintComponentLifecycle.ACTIVATE -> component.onActivate()
                BlueprintComponentLifecycle.UPDATE -> component.onUpdate()
                BlueprintComponentLifecycle.DEACTIVATE -> component.onDeactivate()
                BlueprintComponentLifecycle.DISABLE -> component.onDisable()
                BlueprintComponentLifecycle.DESTROY -> component.onDestroy()
            }
        }
    }

    private fun <T : IBlueprintComponent> scanComponentLifecycleMethods(type: Class<T>): HashSet<BlueprintComponentLifecycle> {
        val lifecycles = HashSet<BlueprintComponentLifecycle>()
        val declaredMethods = type.javaClass.declaredMethods
        for (declaredMethod in declaredMethods) {
            blueprintComponentLifecycleMethodNames[declaredMethod.name]?.let { lifecycle ->
                val parameterTypes = declaredMethod.parameterTypes
                if (parameterTypes.isNotEmpty()) return@let
                lifecycles += lifecycle
            }
        }

        if (lifecycles.size != blueprintComponentLifecycleMethodNames.size) {
            scanParentComponentLifecycleMethods(type.superclass, lifecycles)
        }
        return lifecycles
    }

    private fun scanParentComponentLifecycleMethods(type: Class<*>?, lifecycles: HashSet<BlueprintComponentLifecycle>) {
        if (type == null || type == IBlueprintComponent::class.java || type == Any::class.java) return

        val declaredMethods = type.javaClass.declaredMethods
        for (declaredMethod in declaredMethods) {
            blueprintComponentLifecycleMethodNames[declaredMethod.name]?.let { lifecycle ->
                if (lifecycles.contains(lifecycle)) return@let

                val parameterTypes = declaredMethod.parameterTypes
                if (parameterTypes.isNotEmpty()) return@let
                lifecycles += lifecycle
            }
        }

        if (lifecycles.size != blueprintComponentLifecycleMethodNames.size) {
            scanParentComponentLifecycleMethods(type.superclass, lifecycles)
        }
    }
    
    companion object {
        val blueprintComponentLifecycleMethodNames: Map<String, BlueprintComponentLifecycle> =
            BlueprintComponentLifecycle.values().associateBy { it.methodName }
    }
    
}