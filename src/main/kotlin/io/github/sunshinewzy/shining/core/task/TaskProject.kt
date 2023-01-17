package io.github.sunshinewzy.shining.core.task

import io.github.sunshinewzy.shining.core.data.DataManager
import io.github.sunshinewzy.shining.core.data.DataManager.getTaskProgress
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.inventoryholder.SProtectInventoryHolder
import io.github.sunshinewzy.shining.utils.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

class TaskProject(
    val id: String,
    val projectName: String,
    val openItem: ItemStack = SItem(Material.ENCHANTED_BOOK, "§e$projectName §a向导"),
    val isFirstJoinGive: Boolean = true,
    val title: String,
    val edgeItem: ItemStack = SItem(Material.WHITE_STAINED_GLASS_PANE),
    val openSound: Sound = Sound.ENTITY_HORSE_ARMOR,
    val volume: Float = 1f,
    val pitch: Float = 1.2f,
    val invSize: Int = 5
) : TaskInventory {
    private val holder = SProtectInventoryHolder(id)
    
    val stageMap = HashMap<String, TaskStage>()
    val lastTaskInv = HashMap<UUID, TaskInventory>()
    
    init {
//        DataManager.sTaskData[projectName] = STaskData(this)
        
        if(isFirstJoinGive)
            DataManager.firstJoinGiveOpenItems[id] = openItem

        
        subscribeEvent<PlayerInteractEvent>(ignoreCancelled = false) {
            val item = item ?: return@subscribeEvent

            if(hand == EquipmentSlot.HAND){
                when(action) {
                    Action.LEFT_CLICK_BLOCK -> {
                        if(item.isItemSimilar(openItem)){
                            isCancelled = true

                            openTaskInv(player)
                        }
                    }
                    
                    Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                        if(item.isItemSimilar(openItem)){
                            isCancelled = true

                            lastTaskInv[player.uniqueId]?.let { lastInv ->
                                lastInv.openTaskInv(player)
                                val holder = player.openInventory.topInventory.holder
                                if(holder is TaskInventoryHolder && holder.page > 1) {
                                    holder.page = 1
                                    if(player.isSneaking && player.isOp) {
                                        holder.isEditMode = true
                                        
                                    }
                                }
                                
                                return@subscribeEvent
                            }

                            openTaskInv(player)
                        }
                    }
                    
                    else -> {}
                }
            }
        }
        
        subscribeEvent<InventoryClickEvent> { 
            if(inventory.holder == this@TaskProject.holder){
                stageMap.values.forEach { 
                    val player = view.asPlayer()
                    if(rawSlot == it.order && player.hasCompleteStage(it.predecessor)){
                        it.openTaskInv(player)
                    }
                }
            }
        }
    }


    override fun openTaskInv(player: Player, inv: Inventory) {
        lastTaskProject[player.uniqueId] = this
        lastTaskInv[player.uniqueId] = this
        
        player.playSound(player.location, openSound, volume, pitch)
        player.openInventory(inv)
    }
    
    override fun getTaskInv(player: Player): Inventory {
        val inv = Bukkit.createInventory(holder, invSize * 9, title)
        inv.createEdge(invSize, edgeItem)
        stageMap.values.forEach {
            val pre = it.predecessor
            if(pre == null || player.hasCompleteStage(pre)){
                inv.setItem(it.order, it.symbol)
            }
        }

        return inv
    }
    
    fun getProgress(player: Player): TaskProgress {
        val uid = player.uniqueId.toString()
        
        return player.getTaskProgress(id)
    }

    fun completeAllTask(player: Player, isSilent: Boolean = true) {
        stageMap.values.forEach { 
            it.completeAllTask(player, isSilent)
        }
    }
    
    
    companion object {
        val lastTaskProject = HashMap<UUID, TaskProject>()
        val editItem = SItem(Material.NETHER_STAR, "§f> §c编辑模式 §f<", "§a----------", "§a点我自定义任务！", "§a----------")
    }
    
}