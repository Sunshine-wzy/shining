package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.api.guide.draft.IGuideDraft
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import taboolib.platform.util.buildItem

class GuideDraft(id: EntityID<Long>) : LongEntity(id), IGuideDraft {
    
    var state: IGuideElementState by GuideDrafts.state


    override fun getSymbol(player: Player): ItemStack {
        return buildItem(state.symbol ?: ItemStack(Material.PAPER)) {
            
        }
    }
    

    companion object : LongEntityClass<GuideDraft>(GuideDrafts)
    
}