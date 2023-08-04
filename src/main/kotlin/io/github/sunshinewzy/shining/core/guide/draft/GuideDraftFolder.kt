package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.draft.IGuideDraft
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.data.JacksonWrapper
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor
import io.github.sunshinewzy.shining.core.guide.context.EmptyGuideContext
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.lang.sendPrefixedLangText
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.onBackMenu
import io.github.sunshinewzy.shining.core.menu.openDeleteConfirmMenu
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.orderWith
import io.github.sunshinewzy.shining.utils.toCurrentLocalizedItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import taboolib.common.platform.function.submit
import taboolib.common.util.sync
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class GuideDraftFolder(id: EntityID<Long>) : LongEntity(id), IGuideDraft {
    
    var name: String by GuideDraftFolders.name
    var list: JacksonWrapper<HashSet<GuideDraftFolderNode>> by GuideDraftFolders.list

    private val previousFolderMap: MutableMap<UUID, GuideDraftFolder> = ConcurrentHashMap()


    override fun getSymbol(player: Player): ItemStack {
        return buildItem(Material.BOOK) {
            this.name = this@GuideDraftFolder.name
            lore += player.getLangText("menu-shining_guide-draft-symbol-folder")
            colored()
        }
    }

    override suspend fun open(player: Player, previousFolder: GuideDraftFolder?) {
        if (previousFolder != null)
            previousFolderMap[player.uniqueId] = previousFolder
        
        ShiningGuideDraft.recordLastOpenFolder(player, this)
        
        openMenu(player, previousFolder)
    }
    
    suspend fun openMenu(player: Player, previousFolder: GuideDraftFolder? = null) {
        val subList = getSubList()

        submit {
            player.openMultiPageMenu<IGuideDraft>(player.getLangText("menu-shining_guide-draft-title")) {
                elements { subList }
                
                onGenerate(async = true) { player, element, _, _ -> 
                    element.getSymbol(player)
                }
                
                onClick { _, element -> 
                    ShiningDispatchers.launchDB {
                        element.open(player, this@GuideDraftFolder)
                    }
                }

                // Create folder button
                set(8 orderWith 1, itemCreateFolder.toLocalizedItem(player)) {
                    ShiningDispatchers.launchDB {
                        newSuspendedTransaction {
                            GuideDraftFolder.new {
                                name = ""
                                list = JacksonWrapper(HashSet())
                            }.also {
                                ShiningDispatchers.launchDB {
                                    addFolder(it.id.value)
                                    submit {
                                        it.openFolderEditor(player)
                                    }
                                }
                            }
                        }
                    }
                }
                
                if (this@GuideDraftFolder !== mainFolder) {
                    // Folder editor button
                    set(7 orderWith 1, itemEditFolder.toLocalizedItem(player)) {
                        openFolderEditor(player)
                    }
                    
                    // Back button
                    onBack(item = ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                        if (clickEvent().isShiftClick) {
                            ShiningGuideDraft.openMainMenu(player)
                        } else {
                            back(player)
                        }
                    }
                } else {
                    onBackMenu(player, GuideTeam.CompletedTeam, slot = 2 orderWith 1)
                }
            }
        }
    }

    suspend fun openSelectMenu(player: Player, context: GuideContext, previousFolder: GuideDraftFolder? = null) {
        if (previousFolder != null)
            previousFolderMap[player.uniqueId] = previousFolder
        ShiningGuideDraft.recordLastOpenFolder(player, this)

        val subList = context[GuideDraftContext.OnlyFolders]
            ?.let { getSubFolders() } ?: getSubList()

        submit {
            player.openMultiPageMenu<IGuideDraft>(player.getLangText("menu-shining_guide-draft-title")) {
                elements { subList }
                
                onGenerate(async = true) { player, element, _, _ -> 
                    element.getSymbol(player)
                }
                
                onClick { event, element -> 
                    if (ShiningGuideDraft.isPlayerSelectModeEnabled(player)) {
                        
                        context[GuideDraftContext.Save]?.let { ctxt ->
                            if (element is GuideDraftFolder) {
                                ShiningDispatchers.launchDB {
                                    newSuspendedTransaction {
                                        GuideDraft.new { this.state = JacksonWrapper(ctxt.state) }
                                            .also {
                                                ShiningDispatchers.launchDB {
                                                    newSuspendedTransaction {
                                                        element.addDraft(it.id.value)
                                                    }
                                                }
                                            }
                                        
                                        submit { 
                                            ctxt.state.openEditor(player, ctxt.team, ctxt.context)
                                        }
                                    }
                                }
                            }
                        }
                        
                        context[GuideDraftContext.MoveFolder]?.let { ctxt ->
                            if (element is GuideDraftFolder) {
                                ShiningDispatchers.launchDB {
                                    ctxt.draft.move(ctxt.previousFolder, element)
                                    ctxt.draft.open(player, ctxt.previousFolder)
                                }
                            }
                        }
                        
                        context[GuideDraftContext.Load]?.let { ctxt ->
                            if (element is GuideDraft) {
                                ShiningDispatchers.launchDB { 
                                    val state = newSuspendedTransaction { element.state.value }
                                    val theId = state.id ?: return@launchDB
                                    
                                    submit {
                                        if (ctxt.element != null) {
                                            val oldId = ctxt.element.getId()
                                            if (oldId == theId) {
                                                if (ctxt.element.update(state)) {
                                                    ShiningDispatchers.launchDB { 
                                                        GuideElementRegistry.saveElement(ctxt.element)
                                                        player.sendPrefixedLangText("text-shining_guide-draft-load-success")
                                                    }
                                                } else {
                                                    player.sendPrefixedLangText("text-shining_guide-draft-load-failure-mismatching", Shining.prefix, ctxt.element.javaClass.simpleName, state.javaClass.simpleName)
                                                }
                                            } else {
                                                ShiningDispatchers.launchDB { 
                                                    var isUpdate = true
                                                    if (
                                                        GuideElementRegistry.saveElement(ctxt.element, true, theId) {
                                                            sync {
                                                                if (ctxt.element.update(state)) {
                                                                    true
                                                                } else {
                                                                    player.sendPrefixedLangText("text-shining_guide-draft-load-failure-mismatching", Shining.prefix, ctxt.element.javaClass.simpleName, state.javaClass.simpleName)
                                                                    false
                                                                }
                                                            }.also { isUpdate = it }
                                                        }
                                                    ) {
                                                        ctxt.elementContainer?.let { container ->
                                                            submit {
                                                                container.updateElementId(ctxt.element, oldId)
                                                                ShiningDispatchers.launchDB { 
                                                                    if (GuideElementRegistry.saveElement(container))
                                                                        player.sendPrefixedLangText("text-shining_guide-draft-load-success")
                                                                    else player.sendPrefixedLangText("text-shining_guide-draft-load-failure-save_container")
                                                                }
                                                            }
                                                        } ?: player.sendPrefixedLangText("text-shining_guide-draft-load-success")
                                                    } else if (isUpdate) {
                                                        player.sendPrefixedLangText("text-shining_guide-draft-load-failure-duplication")
                                                    }
                                                }
                                            }
                                        } else if (ctxt.elementContainer != null) {
                                            val theElement = state.toElement()
                                            ShiningDispatchers.launchDB { 
                                                if (GuideElementRegistry.saveElement(theElement, true)) {
                                                    submit {
                                                        ctxt.elementContainer.registerElement(theElement)
                                                        ShiningDispatchers.launchDB {
                                                            if (GuideElementRegistry.saveElement(ctxt.elementContainer))
                                                                player.sendPrefixedLangText("text-shining_guide-draft-load-success")
                                                            else player.sendPrefixedLangText("text-shining_guide-draft-load-failure-save_container")
                                                        }
                                                    }
                                                } else player.sendPrefixedLangText("text-shining_guide-draft-load-failure-save_element")
                                            }
                                        } else if (ctxt.elementContainerState != null) {
                                            val theElement = state.toElement()
                                            ShiningDispatchers.launchDB { 
                                                if (GuideElementRegistry.saveElement(theElement, true)) {
                                                    submit { 
                                                        ctxt.elementContainerState.addElement(theElement)
                                                        player.sendPrefixedLangText("text-shining_guide-draft-load-success")
                                                    }
                                                } else player.sendPrefixedLangText("text-shining_guide-draft-load-failure-save_element")
                                            }
                                        } else {
                                            player.sendPrefixedLangText("text-shining_guide-draft-load-failure-null")
                                        }
                                        
                                        ShiningGuideEditor.openEditor(player, ctxt.team, ctxt.context, ctxt.element, ctxt.elementContainer)
                                    }
                                }
                            }
                        }
                        
                    } else if (element is GuideDraftFolder) {
                        ShiningDispatchers.launchDB {
                            element.openSelectMenu(player, context, this@GuideDraftFolder)
                        }
                    }
                }
                
                // Click empty slot
                if (ShiningGuideDraft.isPlayerSelectModeEnabled(player)) {
                    onClick(lock = true) { event ->
                        if (ShiningGuide.isClickEmptySlot(event)) {
                            context[GuideDraftContext.Save]?.let { ctxt ->
                                ShiningDispatchers.launchDB {
                                    newSuspendedTransaction {
                                        GuideDraft.new { this.state = JacksonWrapper(ctxt.state) }
                                            .also { 
                                                ShiningDispatchers.launchDB {
                                                    newSuspendedTransaction {
                                                        addDraft(it.id.value)
                                                    }
                                                }
                                            }
                                        
                                        submit { 
                                            ctxt.state.openEditor(player, ctxt.team, ctxt.context)
                                        }
                                    }
                                }
                            }
                            
                            context[GuideDraftContext.MoveFolder]?.let { ctxt ->
                                ShiningDispatchers.launchDB {
                                    ctxt.draft.move(ctxt.previousFolder, this@GuideDraftFolder)
                                    ctxt.draft.open(player, ctxt.previousFolder)
                                }
                            }
                        }
                    }
                }
                
                // Select mode button
                set(
                    5 orderWith 1,
                    if (ShiningGuideDraft.isPlayerSelectModeEnabled(player)) ShiningIcon.SELECT_MODE.toStateShinyLocalizedItem("open", player)
                    else ShiningIcon.SELECT_MODE.toStateLocalizedItem("close", player)
                ) {
                    ShiningGuideDraft.switchPlayerSelectMode(player)
                    ShiningDispatchers.launchDB {
                        openSelectMenu(player, context)
                    }
                }
                
                // Create folder button
                set(8 orderWith 1, itemCreateFolder.toLocalizedItem(player)) {
                    ShiningDispatchers.launchDB { 
                        newSuspendedTransaction {
                            GuideDraftFolder.new {
                                name = ""
                                list = JacksonWrapper(HashSet())
                            }.also { 
                                ShiningDispatchers.launchDB {
                                    addFolder(it.id.value)
                                    submit {
                                        it.openFolderEditor(player, context)
                                    }
                                }
                            }
                        }
                    }
                }

                if (this@GuideDraftFolder !== mainFolder) {
                    // Folder editor button
                    set(7 orderWith 1, itemEditFolder.toLocalizedItem(player)) {
                        openFolderEditor(player, context)
                    }

                    // Back button
                    onBack(item = ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                        if (clickEvent().isShiftClick) {
                            ShiningGuideDraft.openMainMenu(player)
                        } else {
                            previousFolderMap[player.uniqueId]?.let {
                                ShiningDispatchers.launchDB {
                                    it.openSelectMenu(player, context)
                                }
                                return@onBack
                            }

                            ShiningGuideDraft.openMainSelectMenu(player, context)
                        }
                    }
                }
            }
        }
    }
    
    fun openFolderEditor(player: Player, context: GuideContext = EmptyGuideContext) {
        ShiningDispatchers.launchDB { 
            newSuspendedTransaction {
                val theName = this@GuideDraftFolder.name
                
                submit {
                    player.openMenu<Basic>(itemCreateFolder.toLocalizedItem(player).getDisplayName()) {
                        rows(3)

                        map(
                            "-B-------",
                            "-  a d  -",
                            "---------"
                        )

                        set('-', ShiningIcon.EDGE.item)

                        set('B', ShiningIcon.BACK.toLocalizedItem(player)) {
                            ShiningDispatchers.launchDB {
                                if (context === EmptyGuideContext) open(player)
                                else openSelectMenu(player, context)
                            }
                        }

                        val itemRename = ShiningIcon.RENAME.getNamespacedIdItem().toCurrentLocalizedItem(player, theName)
                        set('a', itemRename) {
                            player.openChatEditor<Text>(itemRename.getDisplayName()) {
                                text(theName)

                                predicate {
                                    it != MAIN
                                }

                                onSubmit {
                                    ShiningDispatchers.launchDB {
                                        newSuspendedTransaction {
                                            this@GuideDraftFolder.name = it
                                        }
                                    }
                                }

                                onFinal {
                                    openFolderEditor(player, context)
                                }
                            }
                        }

                        previousFolderMap[player.uniqueId]?.let { previousFolder ->
                            set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                                openDeleteFolderConfirmMenu(player, context, previousFolder)
                            }
                        }

                        onClick(lock = true)
                    }
                }
            }
        }
    }
    
    private fun openDeleteFolderConfirmMenu(player: Player, context: GuideContext, previousFolder: GuideDraftFolder) {
        player.openDeleteConfirmMenu { 
            onConfirm { 
                ShiningDispatchers.launchDB {
                    delete(previousFolder)
                    if (context === EmptyGuideContext) open(player)
                    else openSelectMenu(player, context)
                }
            }
            
            onCancel { 
                ShiningDispatchers.launchDB {
                    if (context === EmptyGuideContext) open(player)
                    else openSelectMenu(player, context)
                }
            }
        }
    }
    
    fun back(player: Player) {
        previousFolderMap[player.uniqueId]?.let { 
            ShiningDispatchers.launchDB {
                it.open(player)
            }
            return
        }
        
        ShiningGuideDraft.openMainMenu(player)
    }

    suspend fun add(node: GuideDraftFolderNode) {
        newSuspendedTransaction { 
            list.value.let { 
                it += node
                list = JacksonWrapper(it)
            }
        }
    }
    
    suspend fun add(type: Char, index: Long) {
        add(GuideDraftFolderNode(type, index))
    }
    
    suspend fun add(type: Type, index: Long) {
        add(GuideDraftFolderNode(type.character, index))
    }
    
    suspend fun remove(node: GuideDraftFolderNode) {
        newSuspendedTransaction { 
            list.value.let { 
                it -= node
                list = JacksonWrapper(it)
            }
        }
    }
    
    suspend fun remove(type: Char, index: Long) {
        remove(GuideDraftFolderNode(type, index))
    }
    
    suspend fun remove(type: Type, index: Long) {
        remove(GuideDraftFolderNode(type.character, index))
    }
    
    suspend fun addDraft(index: Long) {
        add(Type.DRAFT, index)
    }
    
    suspend fun removeDraft(index: Long) {
        remove(Type.DRAFT, index)
    }
    
    suspend fun addFolder(index: Long) {
        add(Type.FOLDER, index)
    }
    
    suspend fun removeFolder(index: Long) {
        remove(Type.FOLDER, index)
    }
    
    override suspend fun delete(previousFolder: GuideDraftFolder) {
        previousFolder.removeFolder(id.value)
        val pair = getSubFoldersAndDrafts()
        newSuspendedTransaction {
            pair.first.forEach { folder ->
                folder.delete(this@GuideDraftFolder)
            }
            pair.second.forEach { draft ->
                removeDraft(draft.id.value)
            }
            delete()
        }
    }

    override suspend fun move(previousFolder: GuideDraftFolder, newFolder: GuideDraftFolder) {
        previousFolder.removeFolder(id.value)
        newFolder.addFolder(id.value)
    }

    suspend fun getSubFoldersAndDrafts(): Pair<HashSet<GuideDraftFolder>, HashSet<GuideDraft>> {
        return newSuspendedTransaction { 
            val folderSet = HashSet<GuideDraftFolder>()
            val draftSet = HashSet<GuideDraft>()
            
            list.value.forEach { node -> 
                when (node.type) {
                    Type.FOLDER.character -> {
                        GuideDraftFolder.findById(node.index)?.let { 
                            folderSet += it
                        }
                    }
                    
                    Type.DRAFT.character -> {
                        GuideDraft.findById(node.index)?.let { 
                            draftSet += it
                        }
                    }
                }
            }
            folderSet to draftSet
        }
    }

    suspend fun getSubList(): MutableList<IGuideDraft> {
        val pair = getSubFoldersAndDrafts()
        val subList = ArrayList<IGuideDraft>()
        subList += pair.first
        subList += pair.second
        return subList
    }

    suspend fun getSubFolders(): MutableList<GuideDraftFolder> {
        return newSuspendedTransaction {
            val folders = ArrayList<GuideDraftFolder>()

            list.value.forEach { node ->
                if (node.type == Type.FOLDER.character) {
                    GuideDraftFolder.findById(node.index)?.let {
                        folders += it
                    }
                }
            }
            folders
        }
    }
    
    
    enum class Type(val character: Char) {
        DRAFT('d'),
        FOLDER('f')
    }
    
    
    companion object : LongEntityClass<GuideDraftFolder>(GuideDraftFolders) {
        
        const val MAIN = "main"
        
        private var mainFolder: GuideDraftFolder? = null
        
        private val itemEditFolder = NamespacedIdItem(Material.COMPARATOR, NamespacedId(Shining, "shining_guide-draft-folder-editor"))
        private val itemCreateFolder = NamespacedIdItem(Material.WRITABLE_BOOK, NamespacedId(Shining, "shining_guide-draft-folder-create"))
        
        
        suspend fun getMainFolderOrNull(): GuideDraftFolder? {
            mainFolder?.let { return it }
            
            return newSuspendedTransaction {
                GuideDraftFolder.find { GuideDraftFolders.name eq MAIN }
                    .firstOrNull()
                    ?.also { mainFolder = it }
            }
        }
        
        suspend fun getMainFolder(): GuideDraftFolder {
            getMainFolderOrNull()?.let { return it }
            return newSuspendedTransaction { 
                GuideDraftFolder.new { 
                    name = MAIN
                    list = JacksonWrapper(HashSet())
                }.also { mainFolder = it }
            }
        }
        
    }
    
}