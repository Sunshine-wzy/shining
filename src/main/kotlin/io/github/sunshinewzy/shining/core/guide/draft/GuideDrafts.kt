package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.data.database.column.jackson
import io.github.sunshinewzy.shining.core.guide.state.GuideElementState
import org.jetbrains.exposed.dao.id.LongIdTable

object GuideDrafts : LongIdTable() {
    
    val state = jackson("state", Shining.objectMapper, GuideElementState::class.java)
    
}