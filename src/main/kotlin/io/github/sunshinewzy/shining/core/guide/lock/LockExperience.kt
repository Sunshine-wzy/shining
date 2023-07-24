package io.github.sunshinewzy.shining.core.guide.lock

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.lock.ElementLock
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.state.GuideElementState
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.addLore
import io.github.sunshinewzy.shining.utils.getDisplayName
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic

class LockExperience(
    level: Int,
    isConsume: Boolean = true,
    @JsonIgnore private val levelArray: IntArray = IntArray(1) { level }
) : ElementLock({ it.getLangText("menu-shining_guide-lock-experience-description", levelArray[0].toString()) }, isConsume) {
    
    var level: Int
        get() = levelArray[0]
        set(value) { levelArray[0] = value }
    
    
    override fun check(player: Player): Boolean =
        player.level >= level

    override fun consume(player: Player) {
        player.level -= level
    }

    override fun openEditor(player: Player, team: GuideTeam, state: GuideElementState, context: GuideContext) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-lock-experience-title").colored()) {
            rows(3)

            map(
                "-B-------",
                "- ca d  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                state.openLocksEditor(player, team, context)
            }
            
            set('c', if (isConsume) itemIsConsumeOpen.toLocalizedItem(player) else itemIsConsumeClose.toLocalizedItem(player)) {
                switchIsConsume()
                
                openEditor(player, team, state, context)
            }
            
            set('a', itemEditExperience.toLocalizedItem(player).clone().addLore(description(player))) {
                player.openChatEditor<Text>(itemEditExperience.toLocalizedItem(player).getDisplayName()) {
                    text(level.toString())
                    
                    predicate { 
                        it.toIntOrNull() != null
                    }
                    
                    onSubmit {
                        level = it.toInt()
                    }
                    
                    onFinal { 
                        openEditor(player, team, state, context)
                    }
                }
            }
            
            set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                state.locks -= this@LockExperience
                state.openLocksEditor(player, team)
            }
            
            onClick(lock = true)
        }
    }

    override fun getIcon(player: Player): ItemStack =
        SItem(
            Material.EXPERIENCE_BOTTLE,
            player.getLangText("menu-shining_guide-lock-experience-title"),
            description(player)
        )

    override fun clone(): LockExperience =
        LockExperience(level, isConsume)
    
    
    companion object {
        private val itemEditExperience = NamespacedIdItem(Material.EXPERIENCE_BOTTLE, NamespacedId(Shining, "shining_guide-editor-lock-experience"))
    }
    
}