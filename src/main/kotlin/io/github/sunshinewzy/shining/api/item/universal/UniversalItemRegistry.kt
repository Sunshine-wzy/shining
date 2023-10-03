package io.github.sunshinewzy.shining.api.item.universal

import io.github.sunshinewzy.shining.api.AbstractClassRegistry
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.core.guide.context.AbstractGuideContextElement
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.LanguageItem
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import org.bukkit.entity.Player

object UniversalItemRegistry : AbstractClassRegistry<UniversalItem>() {
    
    fun openCreator(player: Player, context: GuideContext) {
        player.openMultiPageMenu<Pair<Class<out UniversalItem>, LanguageItem>>(player.getLangText("menu-item-universal-creator-title")) { 
            elements { getRegisteredClassPairList() }
            
            onGenerate { _, element, _, _ -> 
                element.second.toLocalizedItem(player)
            }
            
            onClick { _, element -> 
                val theItem = element.first.getConstructor().newInstance()
                context[CreateContext]?.let { it.onCreate(theItem) }
                theItem.openEditor(player, context)
            }
            
            context[GuideEditorContext.Back]?.let { 
                onBack { it.onBack(this) }
            }
        }
    }
    
    
    class CreateContext(val onCreate: (UniversalItem) -> Unit) : AbstractGuideContextElement(CreateContext) {
        companion object : GuideContext.Key<CreateContext>
    }
    
}