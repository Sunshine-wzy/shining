package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.core.data.JacksonWrapper
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class GuideDraftFolder(id: EntityID<Long>) : LongEntity(id) {
    
    var list: JacksonWrapper<HashSet<Pair<Char, Long>>> by GuideDraftFolders.list
    
    
    fun add(pair: Pair<Char, Long>) {
        transaction { 
            list.value.let { 
                it += pair
                list = JacksonWrapper(it)
            }
        }
    }
    
    fun add(type: Char, index: Long) {
        add(type to index)
    }
    
    fun remove(pair: Pair<Char, Long>) {
        transaction { 
            list.value.let { 
                it -= pair
                list = JacksonWrapper(it)
            }
        }
    }
    
    fun remove(type: Char, index: Long) {
        remove(type to index)
    }
    
    fun addDraft(index: Long) {
        add(Type.DRAFT.character, index)
    }
    
    fun removeDraft(index: Long) {
        remove(Type.DRAFT.character, index)
    }
    
    fun addFolder(index: Long) {
        add(Type.FOLDER.character, index)
    }
    
    fun removeFolder(index: Long) {
        remove(Type.FOLDER.character, index)
    }
    
    
    enum class Type(val character: Char) {
        DRAFT('d'),
        FOLDER('f')
    }
    
    
    companion object : LongEntityClass<GuideDraftFolder>(GuideDraftFolders)
    
}