package io.github.sunshinewzy.shining.api.guide.state

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.settings.RepeatableSettings
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.context.EmptyGuideContext
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Represents a captured state of an element, which can describe properties of the element.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
interface IGuideElementState : Cloneable {

    @get:JsonIgnore
    @set:JsonIgnore
    var element: IGuideElement?
    var id: NamespacedId?
    var descriptionName: String?
    var descriptionLore: MutableList<String>
    var symbol: ItemStack
    var repeatableSettings: RepeatableSettings?
    
    /**
     * Updates the state to the element if exists.
     * 
     * @return True when it succeeds.
     */
    fun update(): Boolean

    /**
     * Opens an editor to edit the state.
     */
    fun openEditor(player: Player, team: GuideTeam = GuideTeam.CompletedTeam, context: GuideContext = EmptyGuideContext)

    /**
     * Creates a new element from this state.
     */
    fun toElement(): IGuideElement
    
    /**
     * Gets the element by [id] from [GuideElementRegistry].
     */
    @JsonIgnore
    fun getElementById(): IGuideElement? = id?.let {
        GuideElementRegistry.getElement(it)
    }

    /**
     * Correlates the [element] with this state.
     * 
     * It will set the [id] to the id of the [element],
     * and save the state of the [element] to this state.
     */
    fun correlateElement(element: IGuideElement): IGuideElementState {
        element.saveToState(this)
        return this
    }

    public override fun clone(): IGuideElementState
    
}