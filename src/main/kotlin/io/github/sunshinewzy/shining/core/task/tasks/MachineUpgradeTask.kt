package io.github.sunshinewzy.shining.core.task.tasks

import io.github.sunshinewzy.shining.core.machine.legacy.SMachine
import io.github.sunshinewzy.shining.core.machine.legacy.SMachineSize
import io.github.sunshinewzy.shining.core.machine.legacy.displayInInventory
import io.github.sunshinewzy.shining.core.task.TaskBase
import io.github.sunshinewzy.shining.core.task.TaskStage
import io.github.sunshinewzy.shining.interfaces.MultiPageable
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.utils.*
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class MachineUpgradeTask(
    taskStage: TaskStage,
    id: String,
    taskName: String,
    order: Int,
    predecessor: TaskBase?,
    symbol: ItemStack,
    reward: Array<ItemStack>,
    val sMachine: SMachine,
    val level: Int,
    val requireItems: Array<ItemStack>,
    vararg descriptionLore: String
) : TaskBase(taskStage, id, taskName, order, predecessor, symbol, reward, 5, *descriptionLore), MultiPageable {
    private val size = sMachine.structure.size
    private var flag = true


    init {
        openSound = Sound.UI_BUTTON_CLICK
        volume = 1f
        pitch = 1f

        val yellowGlassPane = SItem(Material.YELLOW_STAINED_GLASS_PANE, 4, 1, " ")
        for(i in 1..5){
            if(i != 3)
                setSlotItem(1, i, yellowGlassPane)
        }
        for(i in 3..7){
            setSlotItem(i, 1, SItem(Material.AIR))
            setSlotItem(i, 5, SItem(Material.AIR))
        }

        setSlotItem(2, 1, yellowGlassPane)
        setSlotItem(2, 5, yellowGlassPane)
        setSubmitItemOrder(8, 1)
        setBackItemOrder(8, 5)

        
        val upgrade = sMachine.structure.getUpgrade(level.toShort()) ?: throw IllegalArgumentException(
            "[MachineUpgradeTask] $taskName: ${sMachine.id} 没有 $level 级"
        )
        
        for(coord in upgrade.keys){
            if(!size.isCoordInSize(coord)){
                flag = false
                break
            }
        }
        
        if(flag){
            when(size) {
                SMachineSize.SIZE3 -> {
                    setSlotItem(9, 1, yellowGlassPane)
                    setSlotItem(9, 5, yellowGlassPane)

                    for(i in 1..3){
                        setSlotItem(9, 5 - i, SItem(
                            Material.GREEN_STAINED_GLASS_PANE, i,
                            "§b⇨ §a查看 第§e$i§a层",
                            "§7Tip:","§f层数自下而上显示"
                        ))
                    }
                }

                SMachineSize.SIZE5 -> {
                    for(i in 1..5){
                        setSlotItem(9, 6 - i, SItem(
                            Material.GREEN_STAINED_GLASS_PANE, i,
                            "§b⇨ §a查看 第§e$i§a层",
                            "§7Tip:","§f层数自下而上显示"
                        ))
                    }
                }
            }
        }
        else{
            for(i in 1..5)
                setSlotItem(9, i, yellowGlassPane)
            
            setSlotItem(1, 3, SItem(Material.STRUCTURE_VOID, "§a插件升级"))
            setSlotItem(9, 3, SItem(Material.STRUCTURE_VOID, "§a插件升级"))
        }
        
    }


    override fun openTaskInv(player: Player, inv: Inventory) {
        taskStage.taskProject.lastTaskInv[player.uniqueId] = this

        player.playSound(player.location, openSound, volume, pitch)
        if(flag && inv.getItem(1 orderWith 3)?.type == Material.GREEN_STAINED_GLASS_PANE){
            player.openInventory(pageInvIn(player, 1))
            return
        }

        sMachine.structure.getUpgrade(level.toShort())?.displayInInventory(inv, 0)
        player.openInventory(inv)
    }

    override fun clickInventory(e: InventoryClickEvent) {
        val player = e.view.asPlayer()
        val x = e.slot.toX(9)
        val y = e.slot.toY(9)

        if(flag && x == 9){
            when(size) {
                SMachineSize.SIZE3 -> {
                    if(y in 2..4)
                        openTaskInv(player, pageInvIn(player, 5 - y))
                }

                SMachineSize.SIZE5 -> {
                    openTaskInv(player, pageInvIn(player, 6 - y))
                }
            }

        }

    }

    override fun pageInvIn(player: Player, page: Int): Inventory {
        val inv = getTaskInv(player)
        val structure = sMachine.structure

        inv.setItem(1, 3, SItem(
            Material.STRUCTURE_VOID,
            page,
            "§a第§e$page§a层",
            "§7Tip:", "§f层数自下而上显示"
        ))

        when(structure.size) {
            SMachineSize.SIZE3 -> {
                inv.setItem(9, 5 - page, SItem(
                    Material.STRUCTURE_VOID, page,
                    "§b⇦ §a当前所在 第§e$page§a层",
                    "§7Tip:","§f层数自下而上显示"
                ))

            }

            SMachineSize.SIZE5 -> {
                inv.setItem(9, 6 - page, SItem(
                    Material.STRUCTURE_VOID, page,
                    "§b⇦ §a当前所在 第§e$page§a层",
                    "§7Tip:","§f层数自下而上显示"
                ))

            }
        }

        structure.displayInInventory(inv, page)
        sMachine.structure.getUpgrade(level.toShort())?.displayInInventory(inv, page)

        return inv
    }

    override fun submit(player: Player) {
        if(player.inventory.containsItem(requireItems))
            completeTask(player)
        else requireNotEnough(player)
    }
}