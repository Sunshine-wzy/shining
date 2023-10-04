package io.github.sunshinewzy.shining.api.guide.state

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.sunshinewzy.shining.api.ShiningAPIProvider
import io.github.sunshinewzy.shining.api.guide.context.EmptyGuideContext
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementRegistry
import io.github.sunshinewzy.shining.api.guide.lock.IElementLock
import io.github.sunshinewzy.shining.api.guide.reward.IGuideReward
import io.github.sunshinewzy.shining.api.guide.settings.RepeatableSettings
import io.github.sunshinewzy.shining.api.guide.team.CompletedGuideTeam
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
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

    var dependencies: MutableSet<NamespacedId>
    var locks: MutableList<IElementLock>
    var rewards: MutableList<IGuideReward>

    
    fun openAdvancedEditor(player: Player, team: IGuideTeam, context: GuideContext)
    
    /**
     * Updates the state to the element if exists.
     * 
     * @return True when it succeeds.
     */
    fun update(): Boolean

    /**
     * Opens an editor to edit the state.
     */
    fun openEditor(player: Player, team: IGuideTeam = CompletedGuideTeam.getInstance(), context: GuideContext = EmptyGuideContext)

    /**
     * Creates a new element from this state.
     */
    fun toElement(): IGuideElement
    
    /**
     * Gets the element by [id] from [IGuideElementRegistry].
     */
    @JsonIgnore
    fun getElementById(): IGuideElement? = id?.let {
        ShiningAPIProvider.api().getGuideElementRegistry().getElement(it)
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

    fun addDependency(element: IGuideElement)

    fun addDependencies(elements: Collection<IGuideElement>)

    @JsonIgnore
    fun getDependencyElements(): List<IGuideElement>
    
    @JsonIgnore
    fun getDependencyElementMapTo(map: MutableMap<NamespacedId, IGuideElement>): MutableMap<NamespacedId, IGuideElement>
    
    @JsonIgnore
    fun getDependencyElementMap(): Map<NamespacedId, IGuideElement> = getDependencyElementMapTo(HashMap())

    fun openBasicEditor(player: Player, team: IGuideTeam, context: GuideContext)

    fun openSymbolEditor(player: Player, team: IGuideTeam, context: GuideContext)

    fun openRepeatableSettingsEditor(player: Player, team: IGuideTeam, context: GuideContext)

    fun openDependenciesEditor(player: Player, team: IGuideTeam, context: GuideContext)

    fun openLocksEditor(player: Player, team: IGuideTeam, context: GuideContext = EmptyGuideContext)

    fun openRewardsEditor(player: Player, team: IGuideTeam, context: GuideContext)

    public override fun clone(): IGuideElementState
    
}