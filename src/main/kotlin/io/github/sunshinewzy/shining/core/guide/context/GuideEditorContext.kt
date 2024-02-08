package io.github.sunshinewzy.shining.core.guide.context

import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer
import io.github.sunshinewzy.shining.core.guide.draft.GuideDraft
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.type.Chest

class GuideEditorContext {
    
    class Back(val onBack: ClickEvent.() -> Unit) : AbstractGuideContextElement(Back) {
        companion object : GuideContext.Key<Back>
    }
    
    class BackNoEvent(val onBack: () -> Unit) : AbstractGuideContextElement(BackNoEvent) {
        companion object : GuideContext.Key<BackNoEvent>
    }
    
    class Builder(val builder: Chest.() -> Unit) : AbstractGuideContextElement(Builder) {
        companion object : GuideContext.Key<Builder>
    }
    
    class Update(val elementContainer: IGuideElementContainer) : AbstractGuideContextElement(Update) {
        companion object : GuideContext.Key<Update>
    }
    
    class Save(val draft: GuideDraft) : AbstractGuideContextElement(Save) {
        companion object : GuideContext.Key<Save>
    }
    
    class Remove(var mode: Boolean = false) : AbstractGuideContextElement(Remove) {
        fun switchMode(): Boolean {
            mode = !mode
            return mode
        }
        
        fun getIcon(player: Player): ItemStack =
            if (mode) ShiningIcon.REMOVE_MODE.toStateShinyLocalizedItem("open", player)
            else ShiningIcon.REMOVE_MODE.toStateLocalizedItem("close", player)
        
        companion object : GuideContext.Key<Remove> {
            fun getOrNew(context: GuideContext): Pair<GuideContext, Remove> {
                context[Remove]?.let { 
                    return context to it
                }
                
                val remove = Remove()
                return (context + remove) to remove
            }
        }
    }
    
}