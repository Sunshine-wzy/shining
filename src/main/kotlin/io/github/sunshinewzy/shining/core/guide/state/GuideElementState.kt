package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.lock.ElementLock
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.Namespace
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.editor.chat.type.TextList
import io.github.sunshinewzy.shining.core.editor.chat.type.TextMap
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.context.EmptyGuideContext
import io.github.sunshinewzy.shining.core.guide.context.GuideSelectElementsContext
import io.github.sunshinewzy.shining.core.guide.context.GuideShortcutBarContext
import io.github.sunshinewzy.shining.core.guide.draft.GuideDraftOnlyFoldersContext
import io.github.sunshinewzy.shining.core.guide.draft.GuideDraftSaveContext
import io.github.sunshinewzy.shining.core.guide.draft.ShiningGuideDraft
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.lock.LockExperience
import io.github.sunshinewzy.shining.core.guide.lock.LockItem
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.lang.sendLangText
import io.github.sunshinewzy.shining.core.menu.onBackMenu
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.insertLore
import io.github.sunshinewzy.shining.utils.orderWith
import io.github.sunshinewzy.shining.utils.toCurrentLocalizedItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.sync
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.isAir
import java.util.*

abstract class GuideElementState : IGuideElementState, Cloneable {

    override var id: NamespacedId? = null
    override var descriptionName: String? = null
    override var descriptionLore: MutableList<String> = LinkedList()
    override var symbol: ItemStack? = null

    var dependencies: MutableSet<NamespacedId> = HashSet()
    var locks: MutableList<ElementLock> = LinkedList()
    
    
    fun addDependency(element: IGuideElement) {
        dependencies += element.getId()
    }
    
    fun addDependencies(elements: Collection<IGuideElement>) {
        elements.forEach { 
            addDependency(it)
        }
    }

    fun copyTo(state: GuideElementState): GuideElementState {
        state.id = id
        state.descriptionName = descriptionName
        state.descriptionLore.clear()
        state.descriptionLore += descriptionLore
        state.symbol = symbol?.clone()
        
        state.dependencies.clear()
        state.dependencies += dependencies
        state.locks.clear()
        locks.mapTo(state.locks) { it.clone() }
        
        return state
    }
    

    abstract fun openAdvancedEditor(player: Player)


    override fun update(): Boolean =
        getElement()?.update(this) ?: false
    
    fun updateAndSave(player: Player?) {
        getElement()?.let { element ->
            id?.let { id ->
                if (id == element.getId()) {
                    update()
                    ShiningDispatchers.launchDB {
                        GuideElementRegistry.saveElement(element)
                        player?.sendLangText("text-shining_guide-editor-state-element-update-success")
                    }
                } else {
                    ShiningDispatchers.launchDB {
                        if (
                            GuideElementRegistry.saveElement(element, true, id) {
                                sync { update() }
                            }
                        ) player?.sendLangText("text-shining_guide-editor-state-element-update-success")
                        else player?.sendLangText("text-shining_guide-editor-state-element-update-failure")
                    }
                }
            }
        }
    }
    
    fun getDependencyElements(): List<IGuideElement> =
        dependencies.mapNotNull { 
            GuideElementRegistry.getElement(it)
        }
    
    fun getDependencyElementMapTo(map: MutableMap<NamespacedId, IGuideElement>): MutableMap<NamespacedId, IGuideElement> {
        dependencies.forEach { id ->
            GuideElementRegistry.getElement(id)?.let {
                map[id] = it
            }
        }
        return map
    }
    
    fun getDependencyElementMap(): Map<NamespacedId, IGuideElement> = getDependencyElementMapTo(HashMap())

    override fun openEditor(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-title")) {
            rows(3)

            map(
                "-B-------",
                "-u a b s-",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)
            
            if (getElement() != null) {
                set('u', itemUpdate.toLocalizedItem(player)) {
                    updateAndSave(player)
                }
            }
            
            set('a', itemBasicEditor.toLocalizedItem(player)) {
                openBasicEditor(player, team, context)
            }

            set('b', itemAdvancedEditor.toLocalizedItem(player)) {
                openAdvancedEditor(player)
            }
            
            set('s', itemSaveToDraft.toLocalizedItem(player)) {
                ShiningGuideDraft.openLastSelectMenu(player, GuideDraftOnlyFoldersContext.INSTANCE + GuideDraftSaveContext(this@GuideElementState, team, context))
            }

            onClick(lock = true)
            
            context[GuideElementStateEditorContext.Back]?.let { 
                it.onBack(this)
            } ?: onBackMenu(player, team)
            
            context[GuideElementStateEditorContext.Builder]?.let { 
                it.builder(this)
            }
        }
    }

    open fun openBasicEditor(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-basic-title")) {
            rows(3)

            map(
                "-B-------",
                "- abcde -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player)) {
                openEditor(player, team, context)
            }

            set('a', itemEditId.toCurrentLocalizedItem(player, "&f$id")) {
                player.openChatEditor<TextMap>(itemEditId.toLocalizedItem(player).getDisplayName()) {
                    id?.also {
                        map(mapOf("namespace" to it.namespace.toString(), "id" to it.id))
                    } ?: map("namespace", "id")

                    predicate {
                        when (index) {
                            "namespace" -> Namespace.VALID_NAMESPACE.matcher(it).matches()
                            "id" -> NamespacedId.VALID_ID.matcher(it).matches()
                            else -> false
                        }
                    }

                    onSubmit { content ->
                        val theNamespace = content["namespace"] ?: return@onSubmit
                        val theId = content["id"] ?: return@onSubmit

                        NamespacedId.fromString("$theNamespace:$theId")?.let {
                            id = it
                        }
                    }

                    onFinal {
                        openBasicEditor(player, team, context)
                    }
                }
            }

            set('b', itemEditDescriptionName.toCurrentLocalizedItem(player, descriptionName)) {
                player.openChatEditor<Text>(itemEditDescriptionName.toLocalizedItem(player).getDisplayName()) {
                    text(descriptionName)

                    onSubmit {
                        descriptionName = content
                    }

                    onFinal {
                        openBasicEditor(player, team, context)
                    }
                }
            }

            set('c', itemEditDescriptionLore.toCurrentLocalizedItem(player, descriptionLore)) {
                player.openChatEditor<TextList>(itemEditDescriptionLore.toLocalizedItem(player).getDisplayName()) {
                    list(descriptionLore)

                    onSubmit {
                        descriptionLore = it
                    }

                    onFinal {
                        openBasicEditor(player, team, context)
                    }
                }
            }

            set('d', itemEditDependencies.toLocalizedItem(player)) {
                openDependenciesEditor(player, team, context)
            }

            set('e', itemEditLocks.toLocalizedItem(player)) {
                openLocksEditor(player, team, context)
            }

            onClick(lock = true)
        }
    }


    fun openDependenciesEditor(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMultiPageMenu<IGuideElement>(player.getLangText("menu-shining_guide-editor-state-basic-dependencies-title")) {
            elements { getDependencyElements() }

            onGenerate(async = true) { player, element, index, slot ->
                element.getSymbolByCondition(player, GuideTeam.CompletedTeam, ElementCondition.UNLOCKED)
                    .insertLore(0, "&7${element.getId()}", "")
            }

            onClick { event, element ->
                openDependencyEditor(player, team, element, context)
            }

            set(2 orderWith 1, ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                openBasicEditor(player, team, context)
            }
            
            onClick(lock = true) {
                if (it.rawSlot in ShiningGuide.slotOrders && it.currentItem.isAir()) {
                    ShiningGuide.openCompletedMainMenu(
                        player,
                        GuideShortcutBarContext() + GuideSelectElementsContext({ element ->
                            this@GuideElementState.getElement()?.let { origin ->
                                if (element === origin) return@GuideSelectElementsContext false
                            }
                            
                            !dependencies.contains(element.getId())
                        }) { ctxt ->
                            addDependencies(ctxt.elements)
                            openDependenciesEditor(player, team, context)
                        }
                    )
                }
            }
        }
    }

    fun openDependencyEditor(player: Player, team: GuideTeam, element: IGuideElement, context: GuideContext) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-basic-dependencies-title")) {
            rows(3)

            map(
                "-B-------",
                "-    d  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                openDependenciesEditor(player, team, context)
            }

            set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                dependencies -= element.getId()
                openDependenciesEditor(player, team, context)
            }

            onClick(lock = true)
        }
    }

    fun openLocksEditor(player: Player, team: GuideTeam, context: GuideContext = EmptyGuideContext) {
        player.openMultiPageMenu<ElementLock>(player.getLangText("menu-shining_guide-editor-state-basic-locks-title")) {
            elements { locks }
            
            onGenerate(async = true) { player, element, _, _ -> 
                element.getIcon(player)
            }
            
            onClick { _, element -> 
                element.openEditor(player, team, this@GuideElementState, context)
            }

            set(2 orderWith 1, ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player)) {
                openBasicEditor(player, team, context)
            }

            onClick(lock = true) {
                if (it.rawSlot in ShiningGuide.slotOrders && it.currentItem.isAir()) {
                    openCreateNewLockMenu(player, team, context)
                }
            }
        }
    }
    
    fun openCreateNewLockMenu(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-basic-locks-create-title")) {
            rows(3)

            map(
                "-B-------",
                "-  a b  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                openLocksEditor(player, team, context)
            }

            set('a', itemCreateLockExperience.toLocalizedItem(player)) {
                val lock = LockExperience(1)
                locks += lock
                lock.openEditor(player, team, this@GuideElementState, context)
            }
            
            set('b', itemCreateLockItem.toLocalizedItem(player)) {
                val lock = LockItem(ItemStack(Material.AIR))
                locks += lock
                lock.openEditor(player, team, this@GuideElementState, context)
            }

            onClick(lock = true)
        }
    }
    
    
    companion object {
        private val itemUpdate = NamespacedIdItem(Material.REDSTONE, NamespacedId(Shining, "shining_guide-editor-state-element-update"))
        private val itemSaveToDraft = NamespacedIdItem(Material.CHEST, NamespacedId(Shining, "shining_guide-editor-state-element-save_to_draft"))
        private val itemBasicEditor = NamespacedIdItem(Material.NAME_TAG, NamespacedId(Shining, "shining_guide-editor-state-element-basic_editor"))
        private val itemAdvancedEditor = NamespacedIdItem(Material.DIAMOND, NamespacedId(Shining, "shining_guide-editor-state-element-advanced_editor"))
        
        private val itemEditId = NamespacedIdItem(Material.NAME_TAG, NamespacedId(Shining, "shining_guide-editor-state-element-id"))
        private val itemEditDescriptionName = NamespacedIdItem(Material.APPLE, NamespacedId(Shining, "shining_guide-editor-state-element-description_name"))
        private val itemEditDescriptionLore = NamespacedIdItem(Material.BREAD, NamespacedId(Shining, "shining_guide-editor-state-element-description_lore"))
        private val itemEditDependencies = NamespacedIdItem(Material.CHEST, NamespacedId(Shining, "shining_guide-editor-state-element-dependencies"))
        private val itemEditLocks = NamespacedIdItem(Material.TRIPWIRE_HOOK, NamespacedId(Shining, "shining_guide-editor-state-element-locks"))

        private val itemCreateLockExperience = NamespacedIdItem(Material.EXPERIENCE_BOTTLE, NamespacedId(Shining, "shining_guide-editor-state-basic-locks-create-experience"))
        private val itemCreateLockItem = NamespacedIdItem(Material.ITEM_FRAME, NamespacedId(Shining, "shining_guide-editor-state-basic-locks-create-item"))
        
    }

}