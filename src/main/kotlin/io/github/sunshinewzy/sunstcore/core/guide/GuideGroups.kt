package io.github.sunshinewzy.sunstcore.core.guide

import com.fasterxml.jackson.core.type.TypeReference
import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.core.data.database.column.jackson
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.*

object GuideGroups : IntIdTable() {
    val name = varchar("name", 50)
    val owner = uuid("owner").uniqueIndex()
    val symbol = jackson("symbol", SunSTCore.objectMapper, ItemStack::class.java)
    val members = jackson("members", SunSTCore.objectMapper, object : TypeReference<HashSet<UUID>>() {})
    val applicants = jackson("applicants", SunSTCore.objectMapper, object : TypeReference<HashSet<UUID>>() {})
    
}