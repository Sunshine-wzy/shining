package io.github.sunshinewzy.shining.core.guide.state

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSetter
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.ElementCondition
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
import io.github.sunshinewzy.shining.core.guide.context.GuideSelectElementsContext
import io.github.sunshinewzy.shining.core.guide.context.GuideShortcutBarContext
import io.github.sunshinewzy.shining.core.guide.draft.ShiningGuideDraft
import io.github.sunshinewzy.shining.core.guide.element.GuideElements
import io.github.sunshinewzy.shining.core.guide.lock.LockExperience
import io.github.sunshinewzy.shining.core.guide.lock.LockItem
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.onBackMenu
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.addLore
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.insertLore
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.isAir
import java.util.*

abstract class GuideElementState : IGuideElementState, Cloneable {

    @JsonIgnore
    override var element: IGuideElement? = null
    override var id: NamespacedId? = null
    override var descriptionName: String? = null
    override var descriptionLore: MutableList<String> = LinkedList()
    override var symbol: ItemStack? = null

    @JsonIgnore
    var dependencyMap: MutableMap<NamespacedId, IGuideElement> = HashMap()
    var locks: MutableList<ElementLock> = LinkedList()

    
    @JsonGetter("dependencies")
    fun getDependenciesId(): MutableSet<NamespacedId> {
        return dependencyMap.keys
    }
    
    @JsonSetter("dependencies")
    fun setDependenciesById(dependencyIds: MutableSet<NamespacedId>) {
        ShiningDispatchers.launchSQL {
            dependencyIds.forEach { id ->
                GuideElements.getElement(id)?.let {
                    dependencyMap[id] = it
                }
            }
        }
    }
    
    @JsonGetter("element")
    fun getElementId(): NamespacedId? {
        return element?.getId()
    }
    
    @JsonSetter("element")
    suspend fun setElementById(elementId: NamespacedId) {
        GuideElements.getElement(elementId)?.let { 
            element = it
        }
    }
    
    
    fun addDependency(element: IGuideElement) {
        dependencyMap[element.getId()] = element
    }
    
    fun addDependencies(elements: Collection<IGuideElement>) {
        elements.forEach { 
            addDependency(it)
        }
    }

    fun copyTo(state: GuideElementState): GuideElementState {
        state.element = element
        
        state.id = id
        state.descriptionName = descriptionName
        state.descriptionLore.clear()
        state.descriptionLore += descriptionLore
        state.symbol = symbol?.clone()
        
        state.dependencyMap.clear()
        state.dependencyMap += dependencyMap
        state.locks.clear()
        locks.mapTo(state.locks) { it.clone() }
        
        return state
    }
    

    abstract fun openAdvancedEditor(player: Player)


    override fun update(): Boolean =
        element?.update(this) ?: false

    override fun openEditor(player: Player, team: GuideTeam) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-title")) {
            rows(3)

            map(
                "-B-------",
                "-u a b s-",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            onBackMenu(player, team)

            set('u', itemUpdate.toLocalizedItem(player)) {
                update()
            }
            
            set('a', itemBasicEditor.toLocalizedItem(player)) {
                openBasicEditor(player, team)
            }

            set('b', itemAdvancedEditor.toLocalizedItem(player)) {
                openAdvancedEditor(player)
            }
            
            set('s', itemSaveToDraft.toLocalizedItem(player)) {
                ShiningGuideDraft.openLastSaveMenu(player, this@GuideElementState)
            }

            onClick(lock = true)
        }
    }

    open fun openBasicEditor(player: Player, team: GuideTeam) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-basic-title")) {
            rows(3)

            map(
                "-B-------",
                "- abcde -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player)) {
                openEditor(player, team)
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
                        openBasicEditor(player, team)
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
                        openBasicEditor(player, team)
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
                        openBasicEditor(player, team)
                    }
                }
            }

            set('d', itemEditDependencies.toLocalizedItem(player)) {
                openDependenciesEditor(player, team)
            }

            set('e', itemEditLocks.toLocalizedItem(player)) {
                openLocksEditor(player, team)
            }

            onClick(lock = true)
        }
    }


    fun openDependenciesEditor(player: Player, team: GuideTeam) {
        player.openMultiPageMenu<IGuideElement>(player.getLangText("menu-shining_guide-editor-state-basic-dependencies-title")) {
            elements { dependencyMap.values.toList() }

            onGenerate(async = true) { player, element, index, slot ->
                element.getSymbolByCondition(player, GuideTeam.CompletedTeam, ElementCondition.UNLOCKED)
                    .insertLore(0, "&7${element.getId()}", "")
            }

            onClick { event, element ->
                openDependencyEditor(player, team, element)
            }

            set(2 orderWith 1, ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                openBasicEditor(player, team)
            }
            
            onClick(lock = true) {
                if (it.rawSlot in ShiningGuide.slotOrders && it.currentItem.isAir()) {
                    ShiningGuide.openCompletedMainMenu(
                        player,
                        GuideShortcutBarContext() + GuideSelectElementsContext({ element ->
                            this@GuideElementState.element?.let { origin ->
                                if (element === origin) return@GuideSelectElementsContext false
                            }
                            
                            !dependencyMap.containsValue(element)
                        }) { ctxt ->
                            addDependencies(ctxt.elements)
                            openDependenciesEditor(player, team)
                        }
                    )
                }
            }
        }
    }

    fun openDependencyEditor(player: Player, team: GuideTeam, element: IGuideElement) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-basic-dependencies-title")) {
            rows(3)

            map(
                "-B-------",
                "-    d  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                openDependenciesEditor(player, team)
            }

            set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                dependencyMap -= element.getId()
                openDependenciesEditor(player, team)
            }

            onClick(lock = true)
        }
    }

    fun openLocksEditor(player: Player, team: GuideTeam) {
        player.openMultiPageMenu<ElementLock>(player.getLangText("menu-shining_guide-editor-state-basic-locks-title")) {
            elements { locks }
            
            onGenerate(async = true) { player, element, _, _ -> 
                element.getIcon(player)
            }
            
            onClick { _, element -> 
                element.openEditor(player, team, this@GuideElementState)
            }

            set(2 orderWith 1, ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player)) {
                openBasicEditor(player, team)
            }

            onClick(lock = true) {
                if (it.rawSlot in ShiningGuide.slotOrders && it.currentItem.isAir()) {
                    openCreateNewLockMenu(player, team)
                }
            }
        }
    }
    
    fun openCreateNewLockMenu(player: Player, team: GuideTeam) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-basic-locks-create-title")) {
            rows(3)

            map(
                "-B-------",
                "-  a b  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                openLocksEditor(player, team)
            }

            set('a', itemCreateLockExperience.toLocalizedItem(player)) {
                val lock = LockExperience(1)
                locks += lock
                lock.openEditor(player, team, this@GuideElementState)
            }
            
            set('b', itemCreateLockItem.toLocalizedItem(player)) {
                val lock = LockItem(ItemStack(Material.AIR))
                locks += lock
                lock.openEditor(player, team, this@GuideElementState)
            }

            onClick(lock = true)
        }
    }


    protected fun ItemStack.addCurrentLore(player: Player, currentLore: String?): ItemStack {
        return addLore("", player.getLangText("menu-shining_guide-editor-state-current_lore"), currentLore ?: "null")
    }

    protected fun ItemStack.addCurrentLore(player: Player, currentLore: List<String>): ItemStack {
        addLore("", player.getLangText("menu-shining_guide-editor-state-current_lore"))
        return addLore(currentLore)
    }

    protected fun NamespacedIdItem.toCurrentLocalizedItem(player: Player, currentLore: String?): ItemStack {
        return toLocalizedItem(player).clone().addCurrentLore(player, currentLore)
    }

    protected fun NamespacedIdItem.toCurrentLocalizedItem(player: Player, currentLore: List<String>): ItemStack {
        return toLocalizedItem(player).clone().addCurrentLore(player, currentLore)
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