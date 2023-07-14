package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.core.data.database.column.jackson
import org.jetbrains.exposed.dao.id.LongIdTable

object GuideDrafts : LongIdTable() {
    
    val state = jackson("state", Shining.objectMapper, IGuideElementState::class.java)
    
}