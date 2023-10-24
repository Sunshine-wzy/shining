package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.state.GuideEmptyState
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit

open class GuideEmpty : GuideElement {
    
    private var defaultComplete: Boolean
    
    
    constructor(
        id: NamespacedId,
        description: ElementDescription,
        symbol: ItemStack,
        defaultComplete: Boolean
    ) : super(id, description, symbol) {
        this.defaultComplete = defaultComplete
    }
    
    constructor() : super() {
        this.defaultComplete = false
    }


    override suspend fun getCondition(team: IGuideTeam): ElementCondition {
        if (defaultComplete) return ElementCondition.COMPLETE
        return super.getCondition(team)
    }

    override fun open(player: Player, team: IGuideTeam, previousElement: IGuideElement?, context: GuideContext) {
        if (defaultComplete) return
        
        ShiningDispatchers.launchDB {
            if (canTeamComplete(team)) {
                submit {
                    complete(player, team)
                }
            }
        }
    }

    override fun openMenu(player: Player, team: IGuideTeam, context: GuideContext) {}

    override suspend fun checkComplete(player: Player, team: IGuideTeam): Boolean {
        return true
    }

    override suspend fun isTeamCompleted(team: IGuideTeam): Boolean {
        if (defaultComplete) return true
        return super.isTeamCompleted(team)
    }

    override fun getState(): IGuideElementState =
        GuideEmptyState().correlateElement(this)

    override fun register(): GuideEmpty = super.register() as GuideEmpty

    override fun saveToState(state: IGuideElementState): Boolean {
        if (state !is GuideEmptyState) return false
        if (!super.saveToState(state)) return false
        
        state.defaultComplete = defaultComplete
        return true
    }

    override fun update(state: IGuideElementState, merge: Boolean): Boolean {
        if (state !is GuideEmptyState) return false
        if (!super.update(state, merge)) return false
        
        defaultComplete = state.defaultComplete
        return true
    }
    
}