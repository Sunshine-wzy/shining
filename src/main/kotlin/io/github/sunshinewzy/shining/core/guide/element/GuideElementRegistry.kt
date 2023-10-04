package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementRegistry
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.data.database.column.jackson
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

object GuideElementRegistry : LongIdTable(), IGuideElementRegistry {
    
    val key = text("key").uniqueIndex()
    val element = jackson("element", Shining.objectMapper, IGuideElementState::class.java)
    
    private val stateCache: MutableMap<NamespacedId, IGuideElementState> = ConcurrentHashMap()
    private val elementCache: MutableMap<NamespacedId, IGuideElement> = ConcurrentHashMap()
    
    
    suspend fun reload() {
        stateCache.clear()
        init()
        elementCache.forEach { (id, element) -> 
            getState(id)?.let { state ->
                element.update(state, true)
            }
        }
        ShiningGuide.reload()
    }

    override fun reloadFuture(): CompletableFuture<Boolean> =
        ShiningDispatchers.futureDB { 
            reload()
            true
        }

    override fun <T: IGuideElement> register(element: T): T {
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

    override fun initFuture(): CompletableFuture<Boolean> =
        ShiningDispatchers.futureDB { 
            init()
            true
        }
    
    override fun getState(id: NamespacedId): IGuideElementState? = stateCache[id]
    
    override fun getElement(id: NamespacedId): IGuideElement? {
        elementCache[id]?.let { return it }
        stateCache[id]?.let {
            val element = it.toElement()
            elementCache[id] = element
            return element
        }
        return null
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T: IGuideElement> getElementByType(id: NamespacedId, type: Class<T>): T? {
        val theElement = getElement(id) ?: return null
        if (type.isInstance(theElement)) return theElement as T
        return null
    }
    
    inline fun <reified T: IGuideElement> getElementByType(id: NamespacedId): T? {
        val theElement = getElement(id) ?: return null
        if (theElement is T) return theElement
        return null
    }
    
    override fun getElementOrDefault(id: NamespacedId, default: IGuideElement): IGuideElement =
        getElement(id) ?: default
    
    override fun getElementOrDefault(default: IGuideElement): IGuideElement =
        getElementOrDefault(default.getId(), default)
    
    override fun getElementCache(): Map<NamespacedId, IGuideElement> = HashMap(elementCache)
    
    suspend fun saveElement(
        element: IGuideElement,
        isCheckExists: Boolean = false,
        checkId: NamespacedId = element.getId(),
        actionBeforeInsert: Supplier<Boolean> = Supplier { true }
    ): Boolean {
        val oldId = element.getId()
        val existsCache = elementCache.containsKey(checkId)
        if (isCheckExists && existsCache)
            return false
        
        return newSuspendedTransaction transaction@{
            val existsDB = containsElement(checkId)
            if (isCheckExists && existsDB)
                return@transaction false

            if (!actionBeforeInsert.get()) return@transaction false
            
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

    override fun saveElementFuture(
        element: IGuideElement,
        isCheckExists: Boolean,
        checkId: NamespacedId,
        actionBeforeInsert: Supplier<Boolean>
    ): CompletableFuture<Boolean> =
        ShiningDispatchers.futureDB { saveElement(element, isCheckExists, checkId, actionBeforeInsert) }
    
    suspend fun removeElement(element: IGuideElement) {
        val id = element.getId()
        stateCache -= id
        elementCache -= id
        newSuspendedTransaction { 
            deleteElement(id)
        }
    }

    override fun removeElementFuture(element: IGuideElement): CompletableFuture<Boolean> =
        ShiningDispatchers.futureDB { 
            removeElement(element)
            true
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