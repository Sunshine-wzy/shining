package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.api.guide.draft.IGuideDraft
import io.github.sunshinewzy.shining.core.data.JacksonWrapper
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import taboolib.common.platform.function.submit
import taboolib.platform.util.buildItem
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class GuideDraftFolder(id: EntityID<Long>) : LongEntity(id), IGuideDraft {
    
    var name: String by GuideDraftFolders.name
    var list: JacksonWrapper<HashSet<Pair<Char, Long>>> by GuideDraftFolders.list

    private val previousFolderMap: MutableMap<UUID, GuideDraftFolder> = ConcurrentHashMap()


    override fun getSymbol(player: Player): ItemStack {
        return buildItem(Material.PAPER) {
            this.name = this@GuideDraftFolder.name
            lore += player.getLangText("menu-shining_guide-draft-symbol-folder")
            colored()
        }
    }

    suspend fun open(player: Player, previousFolder: GuideDraftFolder? = null) {
        if (previousFolder != null)
            previousFolderMap[player.uniqueId] = previousFolder
        
        ShiningGuideDraft.recordLastOpenFolder(player, this)
        
        openMenu(player)
    }
    
    suspend fun openMenu(player: Player) {
        val subList = getSubList()

        submit {
            player.openMultiPageMenu<IGuideDraft> {
                elements { subList }
                
                
            }
        }
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
    
    
    enum class Type(val character: Char) {
        DRAFT('d'),
        FOLDER('f')
    }
    
    
    companion object : LongEntityClass<GuideDraftFolder>(GuideDraftFolders) {
        
        const val MAIN = "main"
        
        private var mainFolder: GuideDraftFolder? = null


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