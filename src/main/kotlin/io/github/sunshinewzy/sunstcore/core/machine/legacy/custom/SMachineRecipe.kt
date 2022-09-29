package io.github.sunshinewzy.sunstcore.core.machine.legacy.custom

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.core.machine.legacy.SMachine
import io.github.sunshinewzy.sunstcore.core.menu.SMenu
import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.isItemSimilar
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.setNameAndLore
import io.github.sunshinewzy.sunstcore.objects.SPosition
import io.github.sunshinewzy.sunstcore.objects.inventoryholder.SInventoryHolder.Companion.getSHolder
import io.github.sunshinewzy.sunstcore.objects.item.SunSTIcon
import io.github.sunshinewzy.sunstcore.objects.legacy.SBlock
import io.github.sunshinewzy.sunstcore.objects.orderWith
import io.github.sunshinewzy.sunstcore.utils.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo

/**
 * 机器配方
 * 
 * 机器功能的描述
 * @param coord 操作位置相对于机器中心方块的坐标
 */
sealed class SMachineRecipe(
    val name: String,
    var coord: SPosition
) : ConfigurationSerializable {
    abstract fun getSymbol(): ItemStack
    
    abstract fun getDisplayItem(): ItemStack
    
    open fun edit(player: Player, sMachine: SMachine, recipes: SMachineRecipes) {
        player.sendMsg(SunSTCore.COLOR_NAME, "&c该配方类型的操作对象无法编辑")
    }
    
    
    /**
     * 消费配方
     * 由外部调用
     */
    fun consume(loc: Location) {
        execute(loc.addClone(coord))
    }
    
    fun consume(loc: Location, player: Player) {
        playerExecute(loc, player)
    }
    
    fun getDisplayItemOrVoid(): ItemStack {
        val display = getDisplayItem()
        if(display.type != Material.AIR) return display
        
        return SItem(Material.STRUCTURE_VOID)
    }

    override fun serialize(): MutableMap<String, Any> {
        val map = HashMap<String, Any>()
        map["coord"] = coord.toString()
        return map
    }

    /**
     * 配方的具体实现执行
     */
    protected open fun execute(loc: Location) {
        
    }
    
    protected open fun playerExecute(loc: Location, player: Player) {
        
    }
    
    
    class BlockPlace(coord: SPosition = SPosition(0, 0, 0), var sBlock: SBlock = SBlock.AIR) : SMachineRecipe(
        NAME,
        coord
    ) {
        override fun getSymbol(): ItemStack = SYMBOL.clone()

        override fun getDisplayItem(): ItemStack = sBlock.getItem().clone()

        override fun edit(player: Player, sMachine: SMachine, recipes: SMachineRecipes) {
            val blockItem = sBlock.getItem().clone()
            
            menu.setClickAction {
                val currentItem = currentItem ?: return@setClickAction

                if(currentItem.type != Material.AIR) {
                    if(slot == 8 orderWith 2 && currentItem.isItemSimilar(confirmItem)) return@setClickAction

                    val item = currentItem.clone()
                    item.amount = 1
                    sBlock = SBlock(item)
                    menu.setButtonWithInv(5, 2, item,"SBLOCK", view.topInventory) {
                        player.giveItem(item)
                    }

                    player.updateInventory()
                }
            }
                .setButton(8, 2, SItem(Material.SLIME_BALL, "§a确认并返回"), "CONFIRM") {
                    recipes.openEditMenu(player, sMachine)
                }
                .setButton(5, 2, blockItem, "SBLOCK") {
                    player.giveItem(blockItem)
                }
            
            
            menu.openInventory(player)
        }

        constructor(map: Map<String, Any>) : this(map["coord"] as? SPosition ?: SPosition(0, 0, 0), map["sBlock"] as? SBlock
            ?: SBlock(Material.AIR)
        )
        
        override fun execute(loc: Location) {
            sBlock.setLocation(loc)
        }

        override fun serialize(): MutableMap<String, Any> {
            return super.serialize().also { 
                it["sBlock"] = sBlock
            }
        }
        
        @SkipTo(LifeCycle.ENABLE)
        companion object {
            const val NAME = "§a方块放置"
            val SYMBOL = SItem(Material.GRASS_BLOCK, NAME)

            private val menu = SMenu("Edit Machine Recipe - BlockPlace", "[机器配方编辑] $NAME BlockPlace", 3)
            private val confirmItem = SItem(Material.SLIME_BALL, "§a确认并返回")
            
            init {
                val whiteGlassPane = SItem(Material.WHITE_STAINED_GLASS_PANE, "§f当前选择方块")
                
                menu.apply { 
                    for(x in 4..6) {
                        setItem(x, 1, whiteGlassPane)
                        setItem(x, 3, whiteGlassPane)
                        if(x != 5) setItem(x, 2, whiteGlassPane)
                    }
                    
                    setItem(2, 2, SItem(Material.NETHER_STAR, "§e请在背包中点击需放置的方块"))
                }
            }
        }
    }
    
    class BlockBreak(coord: SPosition = SPosition(0, 0, 0), var sBlock: SBlock = SBlock.AIR) : SMachineRecipe(
        NAME,
        coord
    ) {
        override fun getSymbol(): ItemStack = SYMBOL.clone()
        
        override fun getDisplayItem(): ItemStack = sBlock.getItem().clone()

        override fun edit(player: Player, sMachine: SMachine, recipes: SMachineRecipes) {
            val blockItem = sBlock.getItem().clone()

            menu.setClickAction {
                val currentItem = currentItem ?: return@setClickAction

                if(currentItem.type != Material.AIR) {
                    if(slot == 8 orderWith 2 && currentItem.isItemSimilar(confirmItem)) return@setClickAction

                    val item = currentItem.clone()
                    item.amount = 1
                    sBlock = SBlock(item)
                    menu.setButtonWithInv(5, 2, item,"SBLOCK", view.topInventory) {
                        player.giveItem(item)
                    }

                    player.updateInventory()
                }
            }
                .setButton(8, 2, confirmItem, "CONFIRM") {
                    recipes.openEditMenu(player, sMachine)
                }
                .setButton(5, 2, blockItem, "SBLOCK") {
                    player.giveItem(blockItem)
                }


            menu.openInventory(player)
        }

        constructor(map: Map<String, Any>) : this(map["coord"] as? SPosition ?: SPosition(0, 0, 0), map["sBlock"] as? SBlock
            ?: SBlock(Material.AIR)
        )

        override fun execute(loc: Location) {
            val block = loc.block
            if(sBlock.isSimilar(block)) {
                block.type = Material.AIR
            }
        }

        override fun serialize(): MutableMap<String, Any> {
            return super.serialize().also { 
                it["sBlock"] = sBlock
            }
        }
        
        @SkipTo(LifeCycle.ENABLE)
        companion object {
            const val NAME = "§c方块破坏"
            val SYMBOL = SItem(Material.IRON_PICKAXE, NAME)
            
            private val menu = SMenu("Edit Machine Recipe - BlockBreak", "[机器配方编辑] $NAME BlockBreak", 3)
            private val confirmItem = SItem(Material.SLIME_BALL, "§a确认并返回")
            
            init {
                val whiteGlassPane = SItem(Material.WHITE_STAINED_GLASS_PANE, "§f当前选择方块")

                menu.apply {
                    for(x in 4..6) {
                        setItem(x, 1, whiteGlassPane)
                        setItem(x, 3, whiteGlassPane)
                        if(x != 5) setItem(x, 2, whiteGlassPane)
                    }

                    setItem(2, 2, SItem(Material.NETHER_STAR, "§e请在背包中点击需破坏的方块"))
                }
            }
        }
    }
    
    class ItemAddPlayer(coord: SPosition = SPosition(0, 0, 0), val items: MutableList<ItemStack> = mutableListOf()) : SMachineRecipe(
        NAME,
        coord
    ) {
        override fun getSymbol(): ItemStack = SYMBOL.clone()
        
        override fun getDisplayItem(): ItemStack = items.cloneFirstOrAir()

        override fun edit(player: Player, sMachine: SMachine, recipes: SMachineRecipes) {

        }

        
        constructor(coord: SPosition, vararg item: ItemStack) : this(coord, item.toMutableList())

        constructor(map: Map<String, Any>) : this(
            map["coord"] as? SPosition ?: SPosition(0, 0, 0),
            map["items"]?.castList<ItemStack>() ?: mutableListOf()
        )

        override fun playerExecute(loc: Location, player: Player) {
            player.giveItem(items)
        }

        override fun serialize(): MutableMap<String, Any> {
            return super.serialize().also { 
                it["items"] = items
            }
        }

        companion object {
            const val NAME = "§a给予玩家物品"
            val SYMBOL = SItem(Material.IRON_INGOT, NAME)
        }
    }
    
    class ItemRemovePlayer(coord: SPosition = SPosition(0, 0, 0), var type: Type = Type.HAND, val items: MutableList<ItemStack> = mutableListOf()) : SMachineRecipe(
        NAME,
        coord
    ) {
        override fun getSymbol(): ItemStack = SYMBOL.clone()
        
        override fun getDisplayItem(): ItemStack = items.cloneFirstOrAir()

        override fun edit(player: Player, sMachine: SMachine, recipes: SMachineRecipes) {

        }


        constructor(coord: SPosition, type: Type, vararg item: ItemStack) : this(coord, type, item.toMutableList())

        constructor(map: Map<String, Any>) : this(
            map["coord"] as? SPosition ?: SPosition(0, 0, 0),
            (map["type"] as? String)?.let { Type.valueOf(it) } ?: Type.HAND,
            map["items"]?.castList<ItemStack>() ?: mutableListOf()
        )
        

        override fun playerExecute(loc: Location, player: Player) {
            val inv = player.inventory
            when(type) {
                Type.HAND -> {
                    items.forEach {
                        inv.removeHandItem(it)
                    }
                }

                Type.OFF_HAND -> {
                    items.forEach {
                        inv.removeOffHandItem(it)
                    }
                }

                Type.INVENTORY -> inv.removeSItem(items)
            }
        }

        override fun serialize(): MutableMap<String, Any> {
            return super.serialize().also { 
                it["type"] = type.name
                it["items"] = items
            }
        }

        enum class Type {
            HAND,
            OFF_HAND,
            INVENTORY
        }

        companion object {
            const val NAME = "§c移除玩家物品"
            val SYMBOL = SItem(Material.APPLE, NAME)
        }
    }
    
    class ItemAddGround(coord: SPosition = SPosition(0, 0, 0), val items: MutableList<ItemStack> = mutableListOf()) : SMachineRecipe(
        NAME,
        coord
    ) {
        override fun getSymbol(): ItemStack = SYMBOL.clone()
        
        override fun getDisplayItem(): ItemStack = items.cloneFirstOrAir()

        override fun edit(player: Player, sMachine: SMachine, recipes: SMachineRecipes) {

        }

        
        constructor(coord: SPosition, vararg item: ItemStack) : this(coord, item.toMutableList())

        constructor(map: Map<String, Any>) : this(
            map["coord"] as? SPosition ?: SPosition(0, 0, 0),
            map["items"]?.castList<ItemStack>() ?: mutableListOf()
        )
        
        override fun execute(loc: Location) {
            loc.world?.let { world ->
                items.forEach { 
                    world.dropItemNaturally(loc, it)
                }
            }
        }

        override fun serialize(): MutableMap<String, Any> {
            return super.serialize().also { 
                it["items"] = items
            }
        }

        companion object {
            const val NAME = "§a生成掉落物"
            val SYMBOL = SItem(Material.GOLD_INGOT, NAME)
        }
    }
    
    class ItemRemoveGround(coord: SPosition = SPosition(0, 0, 0), var x: Double = 1.0, var y: Double = 1.0, var z: Double = 1.0, val items: MutableList<ItemStack> = mutableListOf()) : SMachineRecipe(
        NAME,
        coord
    ) {
        override fun getSymbol(): ItemStack = SYMBOL.clone()
        
        override fun getDisplayItem(): ItemStack = items.cloneFirstOrAir()

        override fun edit(player: Player, sMachine: SMachine, recipes: SMachineRecipes) {

        }
        

        constructor(coord: SPosition, x: Double, y: Double, z: Double, vararg item: ItemStack) : this(coord, x, y, z, item.toMutableList())

        override fun execute(loc: Location) {
            val world = loc.world ?: return
            world.getNearbyEntities(loc, x, y, z).forEach { 
                if(it is Item) {
                    
                }
            }
        }

        companion object {
            const val NAME = "§c移除掉落物"
            val SYMBOL = SItem(Material.BREAD, NAME)
        }
    }
    
    class Other(coord: SPosition = SPosition(0, 0, 0)) : SMachineRecipe(NAME, coord) {
        override fun getSymbol(): ItemStack = SYMBOL.clone()
        
        override fun getDisplayItem(): ItemStack = SYMBOL.clone()

        
        companion object {
            const val NAME = "§d其他"
            val SYMBOL = SItem(Material.EXPERIENCE_BOTTLE, NAME)
        }
    }
    
    object Empty : SMachineRecipe("§7空", SPosition(0, 0, 0)) {
        override fun getSymbol(): ItemStack = SYMBOL.clone()
        
        override fun getDisplayItem(): ItemStack = SYMBOL.clone()

        
        val SYMBOL = SItem(Material.STRUCTURE_VOID, name)
    }

}

data class SMachineRecipes(
    val id: String,
    var input: SMachineRecipe = SMachineRecipe.Empty,
    var output: SMachineRecipe = SMachineRecipe.Empty,
    var percent: Int = 0
) : ConfigurationSerializable {
    
    init {
        require(percent in 0..100) {
            "The percent should be between 0 and 100."
        }
        
        val redGlassPane = SItem(Material.RED_STAINED_GLASS_PANE, "§f输入")
        val greenGlassPane = SItem(Material.LIME_STAINED_GLASS_PANE, "§f输出")
        
        editMenu.apply { 
            for(y in 2..5) {
                setItem(2, y, redGlassPane)
                setItem(4, y, redGlassPane)
                setItem(6, y, greenGlassPane)
                setItem(8, y, greenGlassPane)
            }
            
            setItem(3, 2, redGlassPane)
            setItem(3, 5, redGlassPane)
            setItem(7, 2, greenGlassPane)
            setItem(7, 5, greenGlassPane)
        }
        
        
        fun InventoryClickEvent.setRecipeType(recipe: () -> SMachineRecipe) {
            inventory.getSHolder()?.let {
                val type = it.extra["type"] ?: return@let
                if(type is Type) {
                    when(type) {
                        Type.INPUT -> input = recipe()
                        Type.OUTPUT -> output = recipe()
                    }
                }
                
                val sMachine = it.extra["sMachine"] ?: return@let
                if(sMachine is SMachine) {
                    openEditMenu(view.asPlayer(), sMachine)
                }
            }
        }
        
        typeChooseMenu.apply { 
            setButton(1, 1, SMachineRecipe.BlockPlace.SYMBOL, "BLOCK_PLACE") {
                setRecipeType { SMachineRecipe.BlockPlace() }
            }
            setButton(2, 1, SMachineRecipe.BlockBreak.SYMBOL, "BLOCK_BREAK") {
                setRecipeType { SMachineRecipe.BlockBreak() }
            }
            setButton(3, 1, SMachineRecipe.ItemAddPlayer.SYMBOL, "ITEM_ADD_PLAYER") {
                setRecipeType { SMachineRecipe.ItemAddPlayer() }
            }
            setButton(4, 1, SMachineRecipe.ItemRemovePlayer.SYMBOL, "ITEM_REMOVE_PLAYER") {
                setRecipeType { SMachineRecipe.ItemRemovePlayer() }
            }
            setButton(5, 1, SMachineRecipe.ItemAddGround.SYMBOL, "ITEM_ADD_GROUND") {
                setRecipeType { SMachineRecipe.ItemAddGround() }
            }
            setButton(6, 1, SMachineRecipe.ItemRemoveGround.SYMBOL, "ITEM_REMOVE_GROUND") {
                setRecipeType { SMachineRecipe.ItemRemoveGround() }
            }
            setButton(7, 1, SMachineRecipe.Other.SYMBOL, "OTHER") {
                setRecipeType { SMachineRecipe.Other() }
            }
            setButton(9, 1, SMachineRecipe.Empty.SYMBOL, "EMPTY") {
                setRecipeType { SMachineRecipe.Empty }
            }
        }
    }


    override fun serialize(): MutableMap<String, Any> {
        val map = HashMap<String, Any>()
        
        map["id"] = id
        map["input"] = input
        map["output"] = output
        map["percent"] = percent
        
        return map
    }
    
    
    fun getEditMenu(player: Player, sMachine: SMachine): SMenu {
        editMenu.title = "机器配方编辑 - $id"
        editMenu.setButton(3, 3, input.getSymbol().setNameAndLore("§f>> §e编辑§c输入类型 §f<<", "§f>> 当前类型:", input.name), "INPUT") {
            typeChooseMenu.openInventory(player) {
                extra["type"] = Type.INPUT
                extra["sMachine"] = sMachine
            }
        }
            .setButton(7, 3, output.getSymbol().setNameAndLore("§f>> §e编辑§a输出类型 §f<<", "§f>> 当前类型:", output.name), "OUTPUT") {
                typeChooseMenu.openInventory(player) {
                    extra["type"] = Type.OUTPUT
                    extra["sMachine"] = sMachine
                }
            }
        
            .setButton(3, 4, input.getDisplayItemOrVoid().setNameAndLore("§f>> §e编辑§b操作对象 §f<<"), "INPUT_ITEM") {
                input.edit(player, sMachine, this@SMachineRecipes)
            }
            .setButton(7, 4, output.getDisplayItemOrVoid().setNameAndLore("§f>> §e编辑§b操作对象 §f<<"), "OUTPUT_ITEM") {
                output.edit(player, sMachine, this@SMachineRecipes)
            }
                
            .setButton(7, 6, SItem(Material.BARRIER, "§c返回并保存"), "BACK") {
                sMachine.editRecipe(player)
            }
            .setButton(5, 6, SunSTIcon.HOME.item, "HOME") {
                sMachine.edit(player)
            }
            .setButton(3, 6, SItem(Material.ENDER_PEARL, "§d将此配方类型设为默认", "§a创建配方时将自动采用该种类型搭配"), "DEFAULT") {
                sMachine.defaultRecipes = input::class to output::class
            }
        
        return editMenu
    }
    
    fun openEditMenu(player: Player, sMachine: SMachine) {
        getEditMenu(player, sMachine).openInventory(player)
    }
    
    fun getDisplayItem(): ItemStack {
        if(output !is SMachineRecipe.Empty && output.getDisplayItem().type != Material.AIR) {
            return output.getDisplayItem()
        }
        
        return input.getDisplayItem()
    }


    @SkipTo(LifeCycle.ENABLE)
    companion object {
        private val editMenu = SMenu("Edit Machine Recipe", "机器配方编辑", 6)
        private val typeChooseMenu = SMenu("Choose Machine Recipe Type", "配方类型选择", 1)
        
        @JvmStatic
        fun deserialize(map: Map<String, Any>): SMachineRecipes {
            val information = SMachineRecipes(map["id"] as String)

            map["input"]?.let {
                if(it is SMachineRecipe)
                    information.input = it
            }

            map["output"]?.let {
                if(it is SMachineRecipe)
                    information.output = it
            }

            map["percent"]?.let { 
                if(it is Int)
                    information.percent = it
            }


            return information
        }
    }
    
    enum class Type {
        INPUT,
        OUTPUT
    }
    
}