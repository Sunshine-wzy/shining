package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.data.database.column.jackson
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.ConcurrentHashMap

object GuideElements : LongIdTable() {
    
    val key = text("key").uniqueIndex()
    val element = jackson("element", Shining.objectMapper, IGuideElementState::class.java)
    
    private val cache: MutableMap<NamespacedId, IGuideElement> = ConcurrentHashMap()
    
    
    suspend fun getState(id: NamespacedId): IGuideElementState? {
        return newSuspendedTransaction { 
            readState(id)
        }
    }
    
    suspend fun getElement(id: NamespacedId): IGuideElement? {
        cache[id]?.let { return it }
        
        return newSuspendedTransaction transaction@{
            readElement(id)?.let {
                cache[id] = it
                return@transaction it
            }

            return@transaction null
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    suspend fun <T: IGuideElement> getElementByType(id: NamespacedId, type: Class<T>): T? {
        val theElement = getElement(id) ?: return null
        if (type.isInstance(theElement)) return theElement as T
        return null
    }
    
    suspend inline fun <reified T: IGuideElement> getElementByType(id: NamespacedId): T? {
        val theElement = getElement(id) ?: return null
        if (theElement is T) return theElement
        return null
    }
    
    suspend fun saveElement(element: IGuideElement, isCheckExists: Boolean = false, actionBeforeInsert: () -> Unit = {}): Boolean {
        val existsCache = cache.containsKey(element.getId())
        if (isCheckExists && existsCache)
            return false
        
        return newSuspendedTransaction transaction@{
            val existsSQL = containsElement(element)
            if (isCheckExists && existsSQL)
                return@transaction false

            actionBeforeInsert()
            if (existsSQL) {
                updateElement(element)
            } else {
                insertElement(element)
            }

            if (!existsCache) {
                cache[element.getId()] = element
            }
            true
        }
    }
    
    
    private fun insertElement(element: IGuideElement): EntityID<Long> =
        insertAndGetId {
            it[GuideElements.key] = element.getId().toString()
            it[GuideElements.element] = element.getState()
        }
    
    private fun updateElement(element: IGuideElement): Int =
        update({ GuideElements.key eq element.getId().toString() }) { 
            it[GuideElements.element] = element.getState()
        }
    
    private fun deleteElement(id: NamespacedId): Int =
        deleteWhere { 
            GuideElements.key eq id.toString()
        }

    private fun deleteElement(element: IGuideElement): Int =
        deleteElement(element.getId())
    
    private fun readState(id: NamespacedId): IGuideElementState? =
        GuideElements
            .slice(GuideElements.element)
            .select { GuideElements.key eq id.toString() }
            .firstNotNullOfOrNull {
                it[GuideElements.element]
            }
    
    private fun readElement(id: NamespacedId): IGuideElement? =
        readState(id)?.toElement()
    
    private fun containsElement(id: NamespacedId): Boolean =
        GuideElements
            .slice(GuideElements.key)
            .select { GuideElements.key eq id.toString() }
            .firstOrNull() != null
    
    private fun containsElement(element: IGuideElement): Boolean =
        containsElement(element.getId())
    
}