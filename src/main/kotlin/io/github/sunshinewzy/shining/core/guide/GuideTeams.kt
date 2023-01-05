package io.github.sunshinewzy.shining.core.guide

import com.fasterxml.jackson.core.type.TypeReference
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.data.database.column.jackson
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.*

object GuideTeams : IntIdTable() {
    
    val name = varchar("name", 50)
    val owner = uuid("owner").uniqueIndex()
    val symbol = jackson("symbol", Shining.objectMapper, ItemStack::class.java)
    val members = jackson("members", Shining.objectMapper, object : TypeReference<HashSet<UUID>>() {})
    val applicants = jackson("applicants", Shining.objectMapper, object : TypeReference<HashSet<UUID>>() {})
    
}