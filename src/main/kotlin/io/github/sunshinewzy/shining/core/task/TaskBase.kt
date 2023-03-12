package io.github.sunshinewzy.shining.core.task

import io.github.sunshinewzy.shining.api.Itemable
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.*
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class TaskBase(
    val taskStage: TaskStage,
    val id: String,
    val taskName: String,
    val order: Int,
    val predecessor: TaskBase?,
    private val symbol: ItemStack,
    val reward: Array<ItemStack>,
    val invSize: Int = 5,
    vararg descriptionLore: String
) : ConfigurationSerializable, TaskInventory {
    protected val holder = TaskInventoryHolder(this)

    private val title = taskStage.taskProject.title
    private val slotItems = HashMap<Int, ItemStack>()
    private var submitItemOrder = -1
    private var backItemOrder = -1

    var openSound = taskStage.openSound
    var volume = taskStage.volume
    var pitch = taskStage.pitch
    var submitItem = ShiningIcon.SUBMIT.item.clone().setLore(*descriptionLore)
    var isCreateEdge = true
    var edgeItem = taskStage.edgeItem


    init {
        taskStage.taskMap[id] = this

        subscribeEvent<InventoryClickEvent> {
            val player = view.asPlayer()

            if (inventory.holder == this@TaskBase.holder) {
                when (rawSlot) {
                    submitItemOrder -> {
                        if (!player.hasCompleteTask(this@TaskBase)) {
                            submit(player)
                        } else againSubmitTask(player)
                    }

                    backItemOrder -> {
                        val holder = inventory.holder
                        if (holder is TaskInventoryHolder && holder.page > 1)
                            holder.page = 1

                        taskStage.openTaskInv(player)
                    }
                }

                clickInventory(this)
            }
        }

    }


    override fun serialize(): MutableMap<String, Any> {
        val map = HashMap<String, Any>()

        map["taskStage"] = taskStage.id
        map["taskName"] = id
        map["order"] = order
        map["predecessor"] = predecessor?.id ?: "null"
        map["symbol"] = symbol
        map["reward"] = reward
        map["openSound"] = openSound
        map["volume"] = volume
        map["pitch"] = pitch
        map["invSize"] = invSize
        map["slotItems"] = slotItems

        return map
    }


    override fun openTaskInv(player: Player, inv: Inventory) {
        TaskProject.lastTaskProject[player.uniqueId] = taskStage.taskProject
        taskStage.taskProject.lastTaskInv[player.uniqueId] = this

        player.playSound(player.location, openSound, volume, pitch)
        player.openInventory(inv)
    }

    override fun getTaskInv(player: Player): Inventory {
        val inv = Bukkit.createInventory(holder, invSize * 9, taskName)
        if (isCreateEdge)
            inv.createEdge(invSize, edgeItem)

        slotItems.forEach { (slotOrder, item) ->
            inv.setItem(slotOrder, item)
        }

        return inv
    }


    abstract fun clickInventory(e: InventoryClickEvent)

    abstract fun submit(player: Player)


    fun getSymbol(): ItemStack = symbol.clone()

    fun setSlotItem(order: Int, item: ItemStack): Boolean {
        if (order >= invSize * 9) return false

        slotItems[order] = item
        return true
    }

    fun setSlotItem(order: Int, item: Itemable): Boolean = setSlotItem(order, item.getItemStack())

    fun setSlotItem(x: Int, y: Int, item: ItemStack): Boolean = setSlotItem(x orderWith y, item)

    fun setSlotItem(x: Int, y: Int, item: Itemable): Boolean = setSlotItem(x orderWith y, item.getItemStack())

    fun setSubmitItemOrder(x: Int, y: Int) {
        submitItemOrder = x orderWith y
        setSlotItem(submitItemOrder, submitItem)
    }

    fun setBackItemOrder(x: Int, y: Int) {
        backItemOrder = x orderWith y
        setSlotItem(backItemOrder, ShiningIcon.BACK_MENU)
    }


    fun hasPredecessor(): Boolean = predecessor != null

    open fun againSubmitTask(player: Player) {
        player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, 0.8f)
        player.sendMsg(title, "&c您已完成过任务 &f[&a$taskName&f]&c 了，不能重复提交！")
    }

    open fun requireNotEnough(player: Player) {
        player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, 1.2f)
        player.sendMsg(title, "&c您的背包中没有所需物品！")
    }

    open fun completeTask(player: Player, isSilent: Boolean = false) {
        player.completeTask(this)

        if (isSilent) return

        player.giveItem(reward.clone())
        player.world.playSound(player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 2f)
        player.sendTitle("§f[§e$taskName§f]", "§a任务完成", 10, 70, 20)
        player.closeInventory()
    }


}