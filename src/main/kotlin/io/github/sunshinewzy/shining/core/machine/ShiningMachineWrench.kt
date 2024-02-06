package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.dictionary.IDictionaryItem
import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.dictionary.DictionaryRegistry
import io.github.sunshinewzy.shining.core.lang.item.LocalizedItem
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object ShiningMachineWrench : AbstractMachineWrench() {
    
    private val wrenchItemId = NamespacedId(Shining, "shining_wrench")
    private val wrenchItem = DictionaryRegistry.registerItem(
        wrenchItemId, LocalizedItem(Material.BONE, wrenchItemId),
        object : ItemBehavior() {
            override fun onInteract(event: PlayerInteractEvent, player: Player, item: ItemStack, action: Action) {
                if (event.hand != EquipmentSlot.HAND || action != Action.RIGHT_CLICK_BLOCK) return
                
                val clickedBlock = event.clickedBlock ?: return
                if (clickedBlock.type == Material.AIR) return
                
                event.isCancelled = true
                check(clickedBlock.location, event.blockFace, player)
            }
        }
    )
    
    val itemMachineWrench = NamespacedIdItem(Material.BONE, wrenchItemId)


    override fun getId(): NamespacedId = wrenchItemId
    
    override fun getItemStack(): ItemStack = wrenchItem.getItemStack()
    
    fun getDictionaryItem(): IDictionaryItem = wrenchItem
    
    
    @Awake(LifeCycle.ACTIVE)
    fun onActive() {
        MachineWrenchRegistry.register(this)
    }
    
}