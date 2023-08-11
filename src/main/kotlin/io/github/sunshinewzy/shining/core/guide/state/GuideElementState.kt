package io.github.sunshinewzy.shining.core.guide.state

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer
import io.github.sunshinewzy.shining.api.guide.lock.ElementLock
import io.github.sunshinewzy.shining.api.guide.reward.GuideRewardRegistry
import io.github.sunshinewzy.shining.api.guide.reward.IGuideReward
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.Namespace
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Item
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.editor.chat.type.TextList
import io.github.sunshinewzy.shining.core.editor.chat.type.TextMap
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.context.EmptyGuideContext
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.guide.context.GuideSelectElementsContext
import io.github.sunshinewzy.shining.core.guide.context.GuideShortcutBarContext
import io.github.sunshinewzy.shining.core.guide.draft.GuideDraftContext
import io.github.sunshinewzy.shining.core.guide.draft.ShiningGuideDraft
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.lock.LockExperience
import io.github.sunshinewzy.shining.core.guide.lock.LockItem
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.LanguageItem
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.lang.sendPrefixedLangText
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.onBackMenu
import io.github.sunshinewzy.shining.core.menu.openDeleteConfirmMenu
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.insertLore
import io.github.sunshinewzy.shining.utils.orderWith
import io.github.sunshinewzy.shining.utils.toCurrentLocalizedItem
import kotlinx.coroutines.runBlocking
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.common.util.sync
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.isAir
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class GuideElementState : IGuideElementState {

    private val elementDelegate: ElementDelegate = ElementDelegate()
    
    override var element: IGuideElement? by elementDelegate
    override var id: NamespacedId? = null
    override var descriptionName: String? = null
    override var descriptionLore: MutableList<String> = LinkedList()
    override var symbol: ItemStack = ItemStack(Material.STONE)

    var dependencies: MutableSet<NamespacedId> = HashSet()
    var locks: MutableList<ElementLock> = LinkedList()
    var rewards: MutableList<IGuideReward> = LinkedList()
    
    
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
        state.symbol = symbol.clone()
        
        state.dependencies.clear()
        state.dependencies += dependencies
        state.locks.clear()
        locks.mapTo(state.locks) { it.clone() }
        state.rewards.clear()
        rewards.mapTo(state.rewards) { it.clone() }
        
        return state
    }
    

    abstract fun openAdvancedEditor(player: Player, team: GuideTeam, context: GuideContext)

    override fun update(): Boolean =
        element?.update(this) ?: false
    
    fun updateAndSave(player: Player?, elementContainer: IGuideElementContainer?) {
        val element = element ?: return
        val id = id ?: return
        val oldId = element.getId()
        
        if (id == oldId) {
            update()
            ShiningDispatchers.launchDB {
                GuideElementRegistry.saveElement(element)
                player?.sendPrefixedLangText("text-shining_guide-editor-state-element-update-success")
            }
        } else {
            ShiningDispatchers.launchDB {
                if (
                    GuideElementRegistry.saveElement(element, true, id) {
                        sync { update() }
                    }
                ) {
                    elementContainer?.let { container ->
                        submit {
                            container.updateElementId(element, oldId)
                            ShiningDispatchers.launchDB { 
                                if (GuideElementRegistry.saveElement(container))
                                    player?.sendPrefixedLangText("text-shining_guide-editor-state-element-update-success")
                                else player?.sendPrefixedLangText("text-shining_guide-editor-state-element-update-failure")
                            }
                        }
                    } ?: player?.sendPrefixedLangText("text-shining_guide-editor-state-element-update-success")
                } else player?.sendPrefixedLangText("text-shining_guide-editor-state-element-update-failure")
            }
        }
    }
    
    fun updateElement(): IGuideElement? = elementDelegate.update()
    
    @JsonIgnore
    fun getDependencyElements(): List<IGuideElement> =
        dependencies.mapNotNull { 
            GuideElementRegistry.getElement(it)
        }
    
    @JsonIgnore
    fun getDependencyElementMapTo(map: MutableMap<NamespacedId, IGuideElement>): MutableMap<NamespacedId, IGuideElement> {
        dependencies.forEach { id ->
            GuideElementRegistry.getElement(id)?.let {
                map[id] = it
            }
        }
        return map
    }
    
    @JsonIgnore
    fun getDependencyElementMap(): Map<NamespacedId, IGuideElement> = getDependencyElementMapTo(HashMap())

    override fun openEditor(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-title")) {
            rows(4)

            map(
                "-B-------",
                "-  a b  -",
                "- ust d -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)
            
            val contextUpdate = context[GuideEditorContext.Update]
            val theElement = element
            if (theElement != null && (contextUpdate != null || theElement.getId() == id)) {
                set('u', itemUpdate.toLocalizedItem(player)) {
                    updateAndSave(player, contextUpdate?.elementContainer)
                }
            }
            
            set('a', itemBasicEditor.toLocalizedItem(player)) {
                openBasicEditor(player, team, context)
            }

            set('b', itemAdvancedEditor.toLocalizedItem(player)) {
                openAdvancedEditor(player, team, context)
            }
            
            context[GuideEditorContext.Save]?.let { ctxt ->
                set('s', itemSave.toLocalizedItem(player)) {
                    ShiningDispatchers.launchDB { 
                        ctxt.draft.updateState()
                        player.sendPrefixedLangText("text-shining_guide-editor-state-element-save-success")
                    }
                }
                
                if (theElement != null && (contextUpdate != null || theElement.getId() == id)) {
                    set('t', itemSaveAndUpdate.toLocalizedItem(player)) {
                        ShiningDispatchers.launchDB {
                            ctxt.draft.updateState()
                            player.sendPrefixedLangText("text-shining_guide-editor-state-element-save-success")
                            submit {
                                updateAndSave(player, context[GuideEditorContext.Update]?.elementContainer)
                            }
                        }
                    }
                }
            }
            
            set('d', itemSaveAsDraft.toLocalizedItem(player)) {
                ShiningGuideDraft.openLastSelectMenu(player, GuideDraftContext.OnlyFolders.INSTANCE + GuideDraftContext.Save(this@GuideElementState, team, context))
            }

            onClick(lock = true)
            
            context[GuideEditorContext.Back]?.let { 
                onBack(player) { it.onBack(this) }
            } ?: onBackMenu(player, team)
            
            context[GuideEditorContext.Builder]?.let { 
                it.builder(this)
            }
        }
    }

    open fun openBasicEditor(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-basic-title")) {
            rows(4)

            map(
                "-B-------",
                "- a b c -",
                "- def g  -",
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
                    } ?: map(mapOf("namespace" to "shining", "id" to ""))

                    predicate {
                        when (index) {
                            "namespace" -> Namespace.VALID_NAMESPACE.matcher(it).matches()
                            "id" -> NamespacedId.VALID_ID.matcher(it).matches() && it != "shining_guide"
                            else -> false
                        }
                    }

                    onSubmit { content ->
                        val theNamespace = content["namespace"] ?: return@onSubmit
                        val theId = content["id"] ?: return@onSubmit

                        val namespacedId = NamespacedId.fromString("$theNamespace:$theId") ?: return@onSubmit
                        GuideElementRegistry.getElement(namespacedId)?.let {
                            player.sendPrefixedLangText("text-shining_guide-editor-state-element-basic-id-duplication")
                            return@onSubmit
                        }
                        
                        id = namespacedId
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
            
            set('d', itemEditSymbol.toLocalizedItem(player)) {
                openSymbolEditor(player, team, context)
            }

            set('e', itemEditDependencies.toLocalizedItem(player)) {
                openDependenciesEditor(player, team, context)
            }

            set('f', itemEditLocks.toLocalizedItem(player)) {
                openLocksEditor(player, team, context)
            }
            
            set('g', itemEditRewards.toLocalizedItem(player)) {
                openRewardsEditor(player, team, context)
            }

            onClick(lock = true)
        }
    }

    
    fun openSymbolEditor(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-basic-symbol-title")) { 
            rows(4)
            
            map(
                "-B-------",
                "-   a   -",
                "-   i   -",
                "---------"
            )
            
            set('-', ShiningIcon.EDGE.item)
            
            set('B', ShiningIcon.BACK.toLocalizedItem(player)) {
                openBasicEditor(player, team, context)
            }
            
            val theItemEditSymbolCurrent = itemEditSymbolCurrent.toLocalizedItem(player)
            set('a', theItemEditSymbolCurrent) {
                player.openChatEditor<Item>(theItemEditSymbolCurrent.getDisplayName()) { 
                    item(symbol)
                    
                    onSubmit { 
                        symbol = it
                    }
                    
                    onFinal { 
                        openSymbolEditor(player, team, context)
                    }
                }
            }

            symbol.let { set('i', it) }
            
            onClick(lock = true)
        }
    }

    fun openDependenciesEditor(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMultiPageMenu<IGuideElement>(player.getLangText("menu-shining_guide-editor-state-basic-dependencies-title")) {
            elements { getDependencyElements() }

            onGenerate(true) { player, element, index, slot ->
                runBlocking(ShiningDispatchers.DB) {
                    element.getSymbolByCondition(player, GuideTeam.CompletedTeam, ElementCondition.UNLOCKED)
                        .insertLore(0, "&7${element.getId()}", "")
                }
            }

            onClick { event, element ->
                openDependencyEditor(player, team, element, context)
            }

            set(2 orderWith 1, ShiningIcon.BACK.toLocalizedItem(player)) {
                openBasicEditor(player, team, context)
            }
            
            onClick(lock = true) {
                if (it.rawSlot in ShiningGuide.slotOrders && it.currentItem.isAir()) {
                    ShiningGuide.openCompletedMainMenu(
                        player,
                        GuideShortcutBarContext() + GuideSelectElementsContext({ element ->
                            this@GuideElementState.element?.let { origin ->
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
                "-   d   -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK.toLocalizedItem(player)) {
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
                element.openEditor(player, team, context, this@GuideElementState)
            }

            onBack(player) {
                openBasicEditor(player, team, context)
            }

            onClick(lock = true) {
                if (ShiningGuide.isClickEmptySlot(it)) {
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
                lock.openEditor(player, team, context, this@GuideElementState)
            }
            
            set('b', itemCreateLockItem.toLocalizedItem(player)) {
                val lock = LockItem(ItemStack(Material.AIR))
                locks += lock
                lock.openEditor(player, team, context, this@GuideElementState)
            }

            onClick(lock = true)
        }
    }
    
    fun openRewardsEditor(player: Player, team: GuideTeam, context: GuideContext) {
        val (ctxt, ctxtRemove) = GuideEditorContext.Remove.getOrNew(context)
        
        player.openMultiPageMenu<IGuideReward>(player.getLangText("menu-shining_guide-editor-state-basic-rewards-title")) { 
            elements { rewards }
            
            onGenerate { _, element, _, _ -> element.getIcon(player) }
            
            onClick { _, element -> 
                if (ctxtRemove.mode) {
                    player.openDeleteConfirmMenu { 
                        onConfirm { rewards -= element }
                        onFinal { openRewardsEditor(player, team, ctxt) }
                    }
                } else {
                    element.openEditor(player, GuideEditorContext.BackNoEvent {
                        openRewardsEditor(player, team, ctxt)
                    })
                }
            }
            
            set(8 orderWith 1, ctxtRemove.getIcon(player)) {
                ctxtRemove.switchMode()
                openRewardsEditor(player, team, ctxt)
            }
            
            onBack(player) { openBasicEditor(player, team, context) }
            
            onClick(lock = true) {
                if (ShiningGuide.isClickEmptySlot(it)) {
                    openCreateNewRewardEditor(player, team, context)
                }
            }
        }
    }
    
    fun openCreateNewRewardEditor(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMultiPageMenu<Pair<Class<out IGuideReward>, LanguageItem>>(player.getLangText("menu-shining_guide-editor-state-basic-rewards-create-title")) { 
            elements { GuideRewardRegistry.getRegisteredClassPairList() }
            
            onGenerate { _, element, _, _ -> 
                element.second.toLocalizedItem(player)
            }
            
            onClick { _, element -> 
                val reward = element.first.newInstance()
                rewards += reward
                reward.openEditor(player, GuideEditorContext.BackNoEvent {
                    openRewardsEditor(player, team, context)
                })
            }
            
            onBack { openRewardsEditor(player, team, context) }
        }
    }
    
    
    inner class ElementDelegate : ReadWriteProperty<GuideElementState, IGuideElement?> {
        
        private var elementCache: IGuideElement? = null

        
        override fun getValue(thisRef: GuideElementState, property: KProperty<*>): IGuideElement? {
            if (elementCache == null) elementCache = getElementById()
            return elementCache
        }

        override fun setValue(thisRef: GuideElementState, property: KProperty<*>, value: IGuideElement?) {
            elementCache = value
        }

        fun update(): IGuideElement? {
            elementCache = getElementById()
            return elementCache
        }
        
    }
    
    
    companion object {
        private val itemUpdate = NamespacedIdItem(Material.REDSTONE, NamespacedId(Shining, "shining_guide-editor-state-element-update"))
        private val itemSave = NamespacedIdItem(Material.CHEST, NamespacedId(Shining, "shining_guide-editor-state-element-save"))
        private val itemSaveAndUpdate = NamespacedIdItem(Material.REDSTONE_TORCH, NamespacedId(Shining, "shining_guide-editor-state-element-save_and_update"))
        private val itemSaveAsDraft = NamespacedIdItem(Material.PAPER, NamespacedId(Shining, "shining_guide-editor-state-element-save_as_draft"))
        private val itemBasicEditor = NamespacedIdItem(Material.NAME_TAG, NamespacedId(Shining, "shining_guide-editor-state-element-basic_editor"))
        private val itemAdvancedEditor = NamespacedIdItem(Material.DIAMOND, NamespacedId(Shining, "shining_guide-editor-state-element-advanced_editor"))
        
        private val itemEditId = NamespacedIdItem(Material.NAME_TAG, NamespacedId(Shining, "shining_guide-editor-state-element-id"))
        private val itemEditDescriptionName = NamespacedIdItem(Material.APPLE, NamespacedId(Shining, "shining_guide-editor-state-element-description_name"))
        private val itemEditDescriptionLore = NamespacedIdItem(Material.BREAD, NamespacedId(Shining, "shining_guide-editor-state-element-description_lore"))
        private val itemEditSymbol = NamespacedIdItem(Material.EMERALD, NamespacedId(Shining, "shining_guide-editor-state-element-symbol"))
        private val itemEditSymbolCurrent = NamespacedIdItem(Material.EMERALD, NamespacedId(Shining, "shining_guide-editor-state-element-symbol-current"))
        private val itemEditDependencies = NamespacedIdItem(Material.CHEST, NamespacedId(Shining, "shining_guide-editor-state-element-dependencies"))
        private val itemEditLocks = NamespacedIdItem(Material.TRIPWIRE_HOOK, NamespacedId(Shining, "shining_guide-editor-state-element-locks"))
        private val itemEditRewards = NamespacedIdItem(Material.GOLDEN_APPLE, NamespacedId(Shining, "shining_guide-editor-state-element-rewards"))
        
        private val itemCreateLockExperience = NamespacedIdItem(Material.EXPERIENCE_BOTTLE, NamespacedId(Shining, "shining_guide-editor-state-basic-locks-create-experience"))
        private val itemCreateLockItem = NamespacedIdItem(Material.ITEM_FRAME, NamespacedId(Shining, "shining_guide-editor-state-basic-locks-create-item"))
        
    }

}