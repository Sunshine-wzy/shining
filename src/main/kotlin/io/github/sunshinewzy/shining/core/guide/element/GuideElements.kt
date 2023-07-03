package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.data.database.column.jackson
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

object GuideElements : LongIdTable() {
    
    val key = text("key").uniqueIndex()
    val element = jackson("element", Shining.objectMapper, GuideElement::class.java)
    
    
    fun insertElement(element: GuideElement): EntityID<Long> =
        insertAndGetId { 
            it[GuideElements.key] = element.getId().toString()
            it[GuideElements.element] = element
        }
    
    fun updateElement(element: GuideElement): Int =
        update({ GuideElements.key eq element.getId().toString() }) { 
            it[GuideElements.element] = element
        }
    
    fun deleteElement(id: NamespacedId): Int =
        deleteWhere { 
            GuideElements.key eq id.toString()
        }

    fun deleteElement(element: GuideElement): Int =
        deleteElement(element.getId())
    
    fun getElement(id: NamespacedId): GuideElement? =
        GuideElements
            .slice(GuideElements.element)
            .select { GuideElements.key eq id.toString() }
            .firstNotNullOfOrNull {
                it[GuideElements.element]
            }
    
}