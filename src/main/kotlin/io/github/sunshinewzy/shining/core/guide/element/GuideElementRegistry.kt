package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.data.database.column.jackson
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.ConcurrentHashMap

object GuideElementRegistry : LongIdTable() {
    
    val key = text("key").uniqueIndex()
    val element = jackson("element", Shining.objectMapper, IGuideElementState::class.java)
    
    private val stateCache: MutableMap<NamespacedId, IGuideElementState> = ConcurrentHashMap()
    private val elementCache: MutableMap<NamespacedId, IGuideElement> = ConcurrentHashMap()
    
    
    fun <T: IGuideElement> register(element: T): T {
        val id = element.getId()
        getState(id)?.let { state ->
            element.update(state, true)
        }
        elementCache[id] = element
        return element
    }
    
    suspend fun init() {
        newSuspendedTransaction { 
            GuideElementRegistry
                .slice(element)
                .selectAll()
                .forEach { 
                    val state = it[element]
                    state.id?.let { id ->
                        stateCache[id] = state
                    }
                }
        }
    }
    
    fun getState(id: NamespacedId): IGuideElementState? = stateCache[id]
    
    fun getElement(id: NamespacedId): IGuideElement? {
        elementCache[id]?.let { return it }
        stateCache[id]?.let {
            val element = it.toElement()
            elementCache[id] = element
            return element
        }
        return null
    }
    
    @Suppress("UNCHECKED_CAST")
    fun <T: IGuideElement> getElementByType(id: NamespacedId, type: Class<T>): T? {
        val theElement = getElement(id) ?: return null
        if (type.isInstance(theElement)) return theElement as T
        return null
    }
    
    inline fun <reified T: IGuideElement> getElementByType(id: NamespacedId): T? {
        val theElement = getElement(id) ?: return null
        if (theElement is T) return theElement
        return null
    }
    
    fun getElementOrDefault(id: NamespacedId, default: IGuideElement): IGuideElement =
        getElement(id) ?: default
    
    fun getElementOrDefault(default: IGuideElement): IGuideElement =
        getElementOrDefault(default.getId(), default)
    
    suspend fun saveElement(element: IGuideElement, isCheckExists: Boolean = false, checkId: NamespacedId = element.getId(), actionBeforeInsert: () -> Boolean = { true }): Boolean {
        val oldId = element.getId()
        val existsCache = elementCache.containsKey(checkId)
        if (isCheckExists && existsCache)
            return false
        
        return newSuspendedTransaction transaction@{
            val existsDB = containsElement(checkId)
            if (isCheckExists && existsDB)
                return@transaction false

            if (!actionBeforeInsert()) return@transaction false
            
            if (existsDB) {
                updateElement(element)
            } else {
                insertElement(element)
                if (isCheckExists && checkId != oldId) {
                    transaction {
                        deleteElement(oldId)
                    }
                }
            }

            if (!existsCache) {
                elementCache[element.getId()] = element
            }
            true
        }
    }
    
    
    private fun insertElement(element: IGuideElement): EntityID<Long> =
        insertAndGetId {
            it[key] = element.getId().toString()
            it[GuideElementRegistry.element] = element.getState()
        }
    
    private fun updateElement(element: IGuideElement): Int =
        update({ key eq element.getId().toString() }) { 
            it[GuideElementRegistry.element] = element.getState()
        }
    
    private fun deleteElement(id: NamespacedId): Int =
        deleteWhere { 
            key eq id.toString()
        }

    private fun deleteElement(element: IGuideElement): Int =
        deleteElement(element.getId())
    
    private fun readState(id: NamespacedId): IGuideElementState? =
        GuideElementRegistry
            .slice(element)
            .select { key eq id.toString() }
            .firstNotNullOfOrNull {
                it[element]
            }
    
    private fun readElement(id: NamespacedId): IGuideElement? =
        readState(id)?.toElement()
    
    private fun containsElement(id: NamespacedId): Boolean =
        GuideElementRegistry
            .slice(key)
            .select { key eq id.toString() }
            .firstOrNull() != null
    
    private fun containsElement(element: IGuideElement): Boolean =
        containsElement(element.getId())
    
}