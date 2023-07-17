package io.github.sunshinewzy.shining.api.guide.state

import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Represents a captured state of an element, which can describe properties of the element.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type"
)
interface IGuideElementState {

    var id: NamespacedId?
    var descriptionName: String?
    var descriptionLore: MutableList<String>
    var symbol: ItemStack?
    
    /**
     * Updates the state to the element if exists.
     * 
     * @return True when it succeeds.
     */
    fun update(): Boolean

    /**
     * Opens an editor to edit the state.
     */
    fun openEditor(player: Player, team: GuideTeam = GuideTeam.CompletedTeam)

    fun toElement(): IGuideElement
    
}