package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.draft.IGuideDraft
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.data.JacksonWrapper
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.openDeleteConfirmMenu
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import taboolib.common.platform.function.submit
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class GuideDraftFolder(id: EntityID<Long>) : LongEntity(id), IGuideDraft {
    
    var name: String by GuideDraftFolders.name
    var list: JacksonWrapper<HashSet<Pair<Char, Long>>> by GuideDraftFolders.list

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
                    Shining.launchIO {
                        element.open(player, this@GuideDraftFolder)
                    }
                }

                set(7 orderWith 1, itemEditFolder.toLocalizedItem(player)) {
                    openFolderEditor(player, null)
                }

                set(8 orderWith 1, itemCreateFolder.toLocalizedItem(player)) {
                    Shining.launchIO {
                        newSuspendedTransaction {
                            GuideDraftFolder.new {
                                name = ""
                                list = JacksonWrapper(HashSet())
                            }.also {
                                addFolder(it.id.value)
                                submit {
                                    it.openFolderEditor(player, null)
                                }
                            }
                        }
                    }
                }
                
                onBack(item = ShiningIcon.BACK_MENU.toLocalizedItem(player)) { 
                    if (clickEvent().isShiftClick) {
                        ShiningGuideDraft.openMainMenu(player)
                    } else {
                        back(player)
                    }
                }
            }
        }
    }

    suspend fun openSaveMenu(player: Player, state: IGuideElementState, previousFolder: GuideDraftFolder? = null) {
        if (previousFolder != null)
            previousFolderMap[player.uniqueId] = previousFolder
        ShiningGuideDraft.recordLastOpenFolder(player, this)
        val folders = getSubFolders()

        submit {
            player.openMultiPageMenu<GuideDraftFolder>(player.getLangText("menu-shining_guide-draft-title")) {
                elements { folders }
                
                onGenerate(async = true) { player, element, _, _ -> 
                    element.getSymbol(player)
                }
                
                onClick { event, element -> 
                    if (ShiningGuideDraft.isPlayerSelectModeEnabled(player)) {
                        Shining.launchIO { 
                            newSuspendedTransaction {
                                GuideDraft.new { this.state = state }
                                    .also { element.addDraft(it.id.value) }
                            }
                        }
                    } else {
                        Shining.launchIO {
                            element.openSaveMenu(player, state, this@GuideDraftFolder)
                        }
                    }
                }
                
                if (ShiningGuideDraft.isPlayerSelectModeEnabled(player)) {
                    onClick(lock = true) { event ->
                        if (ShiningGuide.isClickEmptySlot(event)) {
                            Shining.launchIO { 
                                newSuspendedTransaction { 
                                    GuideDraft.new { this.state = state }
                                        .also { addDraft(it.id.value) }
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
                    Shining.launchIO {
                        openSaveMenu(player, state)
                    }
                }
                
                set(7 orderWith 1, itemEditFolder.toLocalizedItem(player)) {
                    openFolderEditor(player, state)
                }
                
                set(8 orderWith 1, itemCreateFolder.toLocalizedItem(player)) {
                    Shining.launchIO { 
                        newSuspendedTransaction {
                            GuideDraftFolder.new {
                                name = ""
                                list = JacksonWrapper(HashSet())
                            }.also { 
                                addFolder(it.id.value)
                                submit {
                                    it.openFolderEditor(player, state)
                                }
                            }
                        }
                    }
                }

                onBack(item = ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                    if (clickEvent().isShiftClick) {
                        ShiningGuideDraft.openMainMenu(player)
                    } else {
                        previousFolderMap[player.uniqueId]?.let {
                            Shining.launchIO {
                                it.openSaveMenu(player, state)
                            }
                            return@onBack
                        }

                        ShiningGuideDraft.openMainSaveMenu(player, state)
                    }
                }
            }
        }
    }
    
    fun openFolderEditor(player: Player, state: IGuideElementState?) {
        player.openMenu<Basic>(itemCreateFolder.toLocalizedItem(player).getDisplayName()) { 
            rows(3)

            map(
                "-B-------",
                "-  a d  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)
            
            set('B', ShiningIcon.BACK.toLocalizedItem(player)) {
                Shining.launchIO {
                    if (state == null) open(player)
                    else openSaveMenu(player, state)
                }
            }
            
            val itemRename = ShiningIcon.RENAME.toLocalizedItem(player)
            set('a', itemRename) {
                Shining.launchIO { 
                    newSuspendedTransaction {
                        val theName = this@GuideDraftFolder.name
                        submit {
                            player.openChatEditor<Text>(itemRename.getDisplayName()) {
                                text(theName)

                                predicate {
                                    it != MAIN
                                }

                                onSubmit {
                                    Shining.launchIO {
                                        newSuspendedTransaction {
                                            this@GuideDraftFolder.name = it
                                        }
                                    }
                                }

                                onFinal {
                                    openFolderEditor(player, state)
                                }
                            }
                        }
                    }
                }
            }
            
            previousFolderMap[player.uniqueId]?.let { previousFolder ->
                set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                    openDeleteFolderConfirmMenu(player, state, previousFolder)
                }
            }
            
            onClick(lock = true)
        }
    }
    
    fun openDeleteFolderConfirmMenu(player: Player, state: IGuideElementState?, previousFolder: GuideDraftFolder) {
        player.openDeleteConfirmMenu { 
            onConfirm { 
                Shining.launchIO {
                    delete(previousFolder)
                    if (state == null) open(player)
                    else openSaveMenu(player, state)
                }
            }
            
            onCancel { 
                Shining.launchIO {
                    if (state == null) open(player)
                    else openSaveMenu(player, state)
                }
            }
        }
    }
    
    fun back(player: Player) {
        previousFolderMap[player.uniqueId]?.let { 
            Shining.launchIO {
                it.open(player)
            }
            return
        }
        
        ShiningGuideDraft.openMainMenu(player)
    }

    suspend fun add(pair: Pair<Char, Long>) {
        newSuspendedTransaction { 
            list.value.let { 
                it += pair
                list = JacksonWrapper(it)
            }
        }
    }
    
    suspend fun add(type: Char, index: Long) {
        add(type to index)
    }
    
    suspend fun add(type: Type, index: Long) {
        add(type.character to index)
    }
    
    suspend fun remove(pair: Pair<Char, Long>) {
        newSuspendedTransaction { 
            list.value.let { 
                it -= pair
                list = JacksonWrapper(it)
            }
        }
    }
    
    suspend fun remove(type: Char, index: Long) {
        remove(type to index)
    }
    
    suspend fun remove(type: Type, index: Long) {
        remove(type.character to index)
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
    
    suspend fun delete(previousFolder: GuideDraftFolder) {
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
    
    suspend fun getSubFoldersAndDrafts(): Pair<HashSet<GuideDraftFolder>, HashSet<GuideDraft>> {
        return newSuspendedTransaction { 
            val folderSet = HashSet<GuideDraftFolder>()
            val draftSet = HashSet<GuideDraft>()
            
            list.value.forEach { pair -> 
                when (pair.first) {
                    Type.FOLDER.character -> {
                        GuideDraftFolder.findById(pair.second)?.let { 
                            folderSet += it
                        }
                    }
                    
                    Type.DRAFT.character -> {
                        GuideDraft.findById(pair.second)?.let { 
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

            list.value.forEach { pair ->
                if (pair.first == Type.FOLDER.character) {
                    GuideDraftFolder.findById(pair.second)?.let {
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