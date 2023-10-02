package io.github.sunshinewzy.shining.core.guide.draft

import com.fasterxml.jackson.core.type.TypeReference
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.data.JacksonWrapper
import io.github.sunshinewzy.shining.core.data.database.column.jackson
import org.jetbrains.exposed.dao.id.LongIdTable

object GuideDraftFolders : LongIdTable() {
    
    val name = varchar("name", 50).index()
    val list = jackson("list", Shining.objectMapper, object : TypeReference<JacksonWrapper<HashSet<GuideDraftFolderNode>>>() {})
    
}