package io.github.sunshinewzy.shining.core.guide.team

import com.fasterxml.jackson.core.type.TypeReference
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.data.database.column.jackson
import io.github.sunshinewzy.shining.core.data.JacksonWrapper
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.*

object GuideTeams : IntIdTable() {

    val name = varchar("name", 50)
    val captain = uuid("captain").uniqueIndex()
    val symbol = jackson("symbol", Shining.objectMapper, ItemStack::class.java)
    val members = jackson("members", Shining.objectMapper, object : TypeReference<JacksonWrapper<HashSet<UUID>>>() {})
    val applicants = jackson("applicants", Shining.objectMapper, object : TypeReference<JacksonWrapper<HashSet<UUID>>>() {})
    val elementData = jackson("element_data", Shining.objectMapper, object : TypeReference<JacksonWrapper<GuideTeamElementData>>() {})
    
}