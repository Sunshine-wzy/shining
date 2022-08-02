package io.github.sunshinewzy.sunstcore.modules.menu

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.events.smenu.SMenuClickEvent
import io.github.sunshinewzy.sunstcore.events.smenu.SMenuOpenEvent
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.isItemSimilar
import io.github.sunshinewzy.sunstcore.objects.SPage
import io.github.sunshinewzy.sunstcore.objects.STurnPageType
import io.github.sunshinewzy.sunstcore.objects.STurnPageType.NEXT_PAGE
import io.github.sunshinewzy.sunstcore.objects.STurnPageType.PRE_PAGE
import io.github.sunshinewzy.sunstcore.objects.inventoryholder.SInventoryHolder
import io.github.sunshinewzy.sunstcore.objects.inventoryholder.SProtectInventoryHolder
import io.github.sunshinewzy.sunstcore.objects.item.SunSTIcon
import io.github.sunshinewzy.sunstcore.objects.orderWith
import io.github.sunshinewzy.sunstcore.utils.actionList
import io.github.sunshinewzy.sunstcore.utils.asPlayer
import io.github.sunshinewzy.sunstcore.utils.subscribeEvent
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * 菜单
 * 
 * @param id 菜单ID
 * @param title 标题
 * @param size 行数
 */
class SMenu(
    val id: String,
    var title: String,
    val size: Int
) {
    private val buttons = HashMap<Int, Pair<String, ItemStack>>()       // 点击触发 SMenuClickEvent 的按钮
    private val items = HashMap<Int, ItemStack>()                       // 普通物品，点击后不会触发事件
    private val buttonOnClick = HashMap<Int, InventoryClickEvent.() -> Unit>()
    private var action: Inventory.() -> Unit = {}
    private var clickAction: InventoryClickEvent.() -> Unit = {}
    private var closeAction: InventoryCloseEvent.() -> Unit = {}
    
    private val pages: HashMap<Int, SPage> by lazy { hashMapOf() }
    private val allTurnPageButtons: HashMap<Int, Pair<STurnPageType, ItemStack>> by lazy { hashMapOf() }
    
    var holder: SInventoryHolder<*> = SProtectInventoryHolder(id)
    var openItem: ItemStack? = null
    var openSound: Sound? = null
    var volume = 1f
    var pitch = 1f
    var maxPage = 0
        set(value) {
            field = value
            holder.maxPage = value
        }
    
    
    init {
        subscribeEvent<InventoryClickEvent> { 
            val holder = inventory.holder ?: return@subscribeEvent
            if(holder is SInventoryHolder<*> && this@SMenu.holder == holder) {
                if(holder.page != 0) {
                    pages[holder.page]?.let { sPage ->
                        sPage.turnPageButtons[rawSlot]?.let {
                            val player = view.asPlayer()

                            when(it.first) {
                                NEXT_PAGE -> if(holder.page < maxPage) openInventoryByPage(player, holder.nextPage())
                                PRE_PAGE -> if(holder.page > 1) openInventoryByPage(player, holder.prePage())
                            }

                            return@subscribeEvent
                        }
                        
                        sPage.buttons[rawSlot]?.let { 
                            it.third(this)
                            SunSTCore.pluginManager.callEvent(SMenuClickEvent(this@SMenu, id, title, view.asPlayer(), rawSlot, it.first, it.second, sPage.page))
                            
                            return@subscribeEvent
                        }
                    }

                    allTurnPageButtons[rawSlot]?.let {
                        val player = view.asPlayer()

                        when(it.first) {
                            NEXT_PAGE -> if(holder.page < maxPage) openInventoryByPage(player, holder.nextPage())
                            PRE_PAGE -> if(holder.page > 1) openInventoryByPage(player, holder.prePage())
                        }

                        return@subscribeEvent
                    }
                }

                buttons[rawSlot]?.let {
                    buttonOnClick[rawSlot]?.invoke(this)
                    SunSTCore.pluginManager.callEvent(SMenuClickEvent(this@SMenu, id, title, view.asPlayer(), rawSlot, it.first, it.second))
                }
                
                try {
                    clickAction(this)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        
        subscribeEvent<PlayerInteractEvent> { 
            val openItem = openItem ?: return@subscribeEvent
            
            if(hand == EquipmentSlot.HAND && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
                val item = item ?: return@subscribeEvent
                if(item.isItemSimilar(openItem)) {
                    val openSound = openSound
                    
                    if(maxPage > 0) {
                        if(openSound != null) openInventoryByPageWithSound(player, 1, openSound, volume, pitch)
                        else openInventoryByPage(player, 1)
                        return@subscribeEvent
                    }
                    
                    if(openSound != null) openInventoryWithSound(player, openSound, volume, pitch)
                    else openInventory(player)
                }
            }
        }
        
        subscribeEvent<InventoryCloseEvent> { 
            val holder = inventory.holder ?: return@subscribeEvent
            if(holder is SInventoryHolder<*> && holder == this@SMenu.holder) {
                closeAction(this)
            }
        }
        
    }
    
    
    fun setButton(slot: Int, item: ItemStack, name: String): SMenu {
        buttons[slot] = name to item
        return this
    }
    
    fun setButton(x: Int, y: Int, item: ItemStack, name: String): SMenu {
        setButton(x orderWith y, item, name)
        return this
    }
    
    fun setButton(slot: Int, item: ItemStack, name: String, onClick: InventoryClickEvent.() -> Unit): SMenu {
        setButton(slot, item, name)
        buttonOnClick[slot] = onClick
        return this
    }

    fun setButton(x: Int, y: Int, item: ItemStack, name: String, onClick: InventoryClickEvent.() -> Unit): SMenu {
        setButton(x orderWith y, item, name, onClick)
        return this
    }
    
    fun setButtonWithInv(slot: Int, item: ItemStack, name: String, inventory: Inventory, onClick: InventoryClickEvent.() -> Unit): SMenu {
        setButton(slot, item, name, onClick)
        inventory.setItem(slot, item)
        return this
    }
    
    fun setButtonWithInv(x: Int, y: Int, item: ItemStack, name: String, inventory: Inventory, onClick: InventoryClickEvent.() -> Unit): SMenu {
        setButtonWithInv(x orderWith y, item, name, inventory, onClick)
        return this
    }
    
    
    fun setItem(slot: Int, item: ItemStack): SMenu {
        items[slot] = item
        return this
    }
    
    fun setItem(x: Int, y: Int, item: ItemStack): SMenu {
        setItem(x orderWith y, item)
        return this
    }
    
    fun setAction(action: Inventory.() -> Unit): SMenu {
        this.action = action
        return this
    }

    fun setClickAction(action: InventoryClickEvent.() -> Unit): SMenu {
        this.clickAction = action
        return this
    }

    fun setCloseAction(action: InventoryCloseEvent.() -> Unit): SMenu {
        this.closeAction = action
        return this
    }
    
    fun setPageAction(page: Int, action: Inventory.() -> Unit): SMenu {
        getSPage(page).action = action
        return this
    }

    fun setPageButton(page: Int, slot: Int, item: ItemStack, name: String, onClick: InventoryClickEvent.() -> Unit): SMenu {
        getSPage(page).buttons[slot] = Triple(name, item, onClick)
        return this
    }

    fun setPageButton(page: Int, slot: Int, item: ItemStack, name: String): SMenu {
        setPageButton(page, slot, item, name) { }
        return this
    }

    fun setPageButton(page: Int, x: Int, y: Int, item: ItemStack, name: String, onClick: InventoryClickEvent.() -> Unit): SMenu {
        setPageButton(page, x orderWith y, item, name, onClick)
        return this
    }
    
    fun setTurnPageButton(page: Int, order: Int, buttonType: STurnPageType, item: ItemStack): SMenu {
        getSPage(page).turnPageButtons[order] = buttonType to item
        return this
    }
    
    fun setTurnPageButton(page: Int, x: Int, y: Int, buttonType: STurnPageType, item: ItemStack): SMenu =
        setTurnPageButton(page, x orderWith  y, buttonType, item)

    fun setPageItem(page: Int, slot: Int, item: ItemStack): SMenu {
        getSPage(page).items[slot] = item
        return this
    }

    fun setPageItem(page: Int, x: Int, y: Int, item: ItemStack): SMenu {
        setPageItem(page, x orderWith y, item)
        return this
    }
    
    fun setAllTurnPageButton(order: Int, buttonType: STurnPageType, item: ItemStack): SMenu {
        allTurnPageButtons[order] = buttonType to item
        return this
    }
    
    fun setAllTurnPageButton(x: Int, y: Int, buttonType: STurnPageType, item: ItemStack): SMenu =
        setAllTurnPageButton(x orderWith y, buttonType, item)
    
    fun setDefaultTurnPageButton(): SMenu {
        setAllTurnPageButton(9, size, NEXT_PAGE, SunSTIcon.PAGE_NEXT.item)
        setAllTurnPageButton(1, size, PRE_PAGE, SunSTIcon.PAGE_PRE.item)
        return this
    }

    /**
     * @param action First Int is page, second Int is order.
     */
    fun <T> setMultiPageAction(startPage: Int, startOrder: Int, endOrder: Int, width: Int, list: List<T>, action: T.(Int, Int) -> Unit) {
        var page = startPage - 1
        var itemList = ArrayList<T>()
        do {
            page++
            setPageAction(page) {
                itemList = actionList(page, startOrder, endOrder, width, list, action)
            }
        } while(itemList.isNotEmpty())
        maxPage = page
    }

    /**
     * @param action First Int is page, second Int is order.
     */
    fun <T> setMultiPageAction(startPage: Int, startX: Int, startY: Int, endX: Int, endY: Int, width: Int, list: List<T>, action: T.(Int, Int) -> Unit) {
        setMultiPageAction(startPage, startX orderWith startY, endX orderWith endY, width, list, action)
    }

    /**
     * @param action First Int is page, second Int is order.
     */
    fun <T> setMultiPageAction(startPage: Int, startX: Int, startY: Int, height: Int, width: Int, list: List<T>, action: T.(Int, Int) -> Unit) {
        setMultiPageAction(startPage, startX orderWith startY, startX orderWith (startY + height), width, list, action)
    }
    
    
    fun getInventory(page: Int = 0): Inventory {
        val holder = holder.clone()
        holder.page = page
        val inv = Bukkit.createInventory(holder, size * 9, title)

        try {
            action(inv)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        
        items.forEach { (slot, item) ->
            inv.setItem(slot, item)
        }
        
        buttons.forEach { (slot, pair) ->
            inv.setItem(slot, pair.second)
        }
        
        return inv
    }
    
    fun openInventory(player: Player) {
        player.openInventory(getInventory())
        SunSTCore.pluginManager.callEvent(SMenuOpenEvent(this, id, title, player))
    }
    
    fun openInventory(player: Player, actionHolder: SInventoryHolder<*>.() -> Unit) {
        val inv = getInventory()
        val holder = inv.holder
        
        if(holder is SInventoryHolder<*>) {
            holder.actionHolder()
        }
        
        player.openInventory(inv)
        SunSTCore.pluginManager.callEvent(SMenuOpenEvent(this, id, title, player))
    }
    
    fun openInventoryWithSound(player: Player, sound: Sound, volume: Float = 1f, pitch: Float = 1f) {
        openInventory(player)
        player.playSound(player.location, sound, volume, pitch)
    }

    fun openInventoryByPage(player: Player, page: Int = 1) {
        val inv = getInventory(page)
        
        allTurnPageButtons.forEach { (order, pair) -> 
            when(pair.first) {
                NEXT_PAGE -> if(page == maxPage) return@forEach
                PRE_PAGE -> if(page == 1) return@forEach
            }
            
            inv.setItem(order, pair.second)
        }

        pages[page]?.let { sPage ->
            try {
                sPage.action(inv)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            
            sPage.turnPageButtons.let { map ->
                map.forEach { (order, pair) ->
                    when(pair.first) {
                        NEXT_PAGE -> if(page == maxPage) return@forEach
                        PRE_PAGE -> if(page == 1) return@forEach
                    }

                    inv.setItem(order, pair.second)
                }
            }
            
            sPage.items.forEach { (order, item) -> 
                inv.setItem(order, item)
            }
            
            sPage.buttons.forEach { (order, triple) -> 
                inv.setItem(order, triple.second)
            }
        }
        
        player.openInventory(inv)
        SunSTCore.pluginManager.callEvent(SMenuOpenEvent(this, id, title, player, page))
    }
    
    fun openInventoryByPageWithSound(player: Player, page: Int, sound: Sound, volume: Float = 1f, pitch: Float = 1f) {
        openInventoryByPage(player, page)
        player.playSound(player.location, sound, volume, pitch)
    }

    fun createEdge(edgeItem: ItemStack) {
        val meta = (if (edgeItem.hasItemMeta()) edgeItem.itemMeta else Bukkit.getItemFactory().getItemMeta(edgeItem.type)) ?: return
        meta.setDisplayName(" ")
        edgeItem.itemMeta = meta

        for(i in 0..8) {
            setItem(i, edgeItem)
            setItem(i + 9 * (size - 1), edgeItem)
        }
        for(i in 9..9*(size - 2) step 9) {
            setItem(i, edgeItem)
            setItem(i + 8, edgeItem)
        }
    }
    
    fun getSPage(page: Int): SPage {
        var sPage = pages[page]
        if(sPage == null) {
            sPage = SPage(page)
            pages[page] = sPage
        }
        
        return sPage
    }
    
}