package io.github.sunshinewzy.shining.core.guide.reward

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.reward.IGuideReward
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.TextList
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.utils.getDisplayName
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.compat.replacePlaceholder

@JsonTypeName("command")
class GuideRewardCommand(val commands: MutableList<String>) : IGuideReward {

    constructor() : this(ArrayList())
    
    
    override fun reward(player: Player) {
        val console = Bukkit.getConsoleSender()
        commands.replacePlaceholder(player).forEach { 
            Bukkit.dispatchCommand(console, it)
        }
    }

    override fun getIcon(player: Player): ItemStack = itemIcon.toLocalizedItem(player)

    override fun openViewMenu(player: Player, context: GuideContext) {
        player.openChatEditor<TextList>(player.getLangText("menu-shining_guide-element-view_rewards-title")) { 
            list(commands)
            
            context[GuideEditorContext.BackNoEvent]?.let { ctxt ->
                onFinal { ctxt.onBack() }
            }
        }
    }

    override fun openEditor(player: Player, context: GuideContext) {
        player.openChatEditor<TextList>(itemIcon.toLocalizedItem(player).getDisplayName()) { 
            list(commands)
            
            onSubmit { 
                commands.clear()
                commands += it
            }
            
            context[GuideEditorContext.BackNoEvent]?.let { ctxt ->
                onFinal { ctxt.onBack() }
            }
        }
    }

    override fun clone(): GuideRewardCommand = GuideRewardCommand(ArrayList(commands))
    
    
    companion object {
        val itemIcon = NamespacedIdItem(Material.COMMAND_BLOCK, NamespacedId(Shining, "shining_guide-reward-command-icon"))
    }
    
}