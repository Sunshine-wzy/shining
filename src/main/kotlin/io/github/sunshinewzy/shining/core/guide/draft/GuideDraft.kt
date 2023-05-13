package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.core.guide.state.GuideElementState
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GuideDraft(id: EntityID<Long>) : LongEntity(id) {
    
    var state: GuideElementState by GuideDrafts.state
    
    
    companion object : LongEntityClass<GuideDraft>(GuideDrafts)
    
}