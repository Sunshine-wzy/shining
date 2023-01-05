package io.github.sunshinewzy.shining.core.machine.legacy

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.data.legacy.internal.SMachineData
import io.github.sunshinewzy.shining.core.machine.legacy.custom.SMachineRecipe
import io.github.sunshinewzy.shining.core.machine.legacy.custom.SMachineRecipes
import io.github.sunshinewzy.shining.core.menu.SMenu
import io.github.sunshinewzy.shining.events.smachine.SMachineAddEvent
import io.github.sunshinewzy.shining.events.smachine.SMachineRemoveEvent
import io.github.sunshinewzy.shining.events.smachine.SMachineUpgradeEvent
import io.github.sunshinewzy.shining.interfaces.Initable
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.SItem.Companion.setNameAndLore
import io.github.sunshinewzy.shining.objects.SLocation
import io.github.sunshinewzy.shining.objects.SLocation.Companion.toSLocation
import io.github.sunshinewzy.shining.objects.item.SunSTIcon
import io.github.sunshinewzy.shining.utils.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import kotlin.reflect.KClass

/**
 * 多方块机器
 * 
 * @param wrench 构建该多方块机器的扳手
 * @param structure 该多方块机器的结构
 */
abstract class SMachine(
    val id: String,
    val name: String,
    val wrench: SMachineWrench,
    val structure: SMachineStructure
) : Initable {
    val sMachines = HashMap<SLocation, SMachineInformation>()
    val recipes = HashMap<String, SMachineRecipes>()
    var defaultRecipes: Pair<KClass<out SMachineRecipe>, KClass<out SMachineRecipe>> = SMachineRecipe.BlockBreak::class to SMachineRecipe.ItemAddGround::class
    val displayItem = structure.centerBlock.toItem().setNameAndLore("§e$name", "§7----------", "§fID: §a$id", "§7----------")
    var isCancelInteract = true
    
    private val editMenu = SMenu("SMachine Edit - $id", "§r[$name§r] 机器编辑", 5)
    val editRecipeMenu = SMenu("SMachine Edit Recipe - $id", "§r[$name§r] 机器配方编辑", 6)
    
    
    init {
        wrench.addMachine(this)

        SMachineData(this)
        
        editMenu.apply { 
            createEdge(SItem(Material.WHITE_STAINED_GLASS_PANE))
            setItem(3, 3, displayItem)

            setButton(5, 5, SunSTIcon.HOME.item, "HOME") {
                wrench.openIllustratedBook(view.asPlayer())
            }
        }
        
        editRecipeMenu.apply { 
            createEdge(SItem(Material.WHITE_STAINED_GLASS_PANE))
            setDefaultTurnPageButton()
            
            setButton(5, 6, SunSTIcon.HOME.item, "HOME") {
                edit(view.asPlayer())
            }
        }
    }


    /**
     * 运行机器
     * 
     * 如果您不在新建一个 像 [SMachineManual] 和 [SMachineTimer] 的 [SMachine] API，请不要重写此函数。
     * 请优先重写函数 [SMachineManual.manualRun] 或 [SMachineTimer.timerRun]
     * 
     * If you are not writing a new [SMachine] API like [SMachineManual] and [SMachineTimer] , please do not override this function.
     * Override the function [SMachineManual.manualRun] or [SMachineTimer.timerRun] first.
     */
    abstract fun runMachine(event: SMachineRunEvent)

    /**
     * 机器编辑界面
     */
    fun edit(player: Player) {
        editMenu.setButton(5, 3, SItem(Material.CRAFTING_TABLE, "§a修改机器配方"), "EDIT_RECIPE") {
            editRecipe(player)
        }
        
        editMenu.openInventory(player)
    }
    
    /**
     * 当编辑机器配方时触发
     */
    fun editRecipe(player: Player) {
        editRecipeMenu.setMultiPageAction(1, 2, 2, 4, 7, recipes.toList()) { page, order ->
            val displayItem = second.getDisplayItem()
            displayItem.setNameAndLore("§f$first", "§7>> 配方类型", "§b输入: ${second.input.name}", "§e输出: ${second.output.name}")
            editRecipeMenu.setPageButton(page, order, displayItem, first) {
                second.openEditMenu(player, this@SMachine)
            }
        }
        
        editRecipeMenu.openInventoryByPage(player)
    }
    

    /**
     * 多方块机器结构判定
     * 
     * @param loc 机器中心位置
     */
    fun judgeStructure(loc: Location, isFirst: Boolean = false, level: Short = 0): Boolean {
        val baseLoc = loc.subtractClone(structure.center)
        
        if(structure.judgeStructure(baseLoc, level)) {
            val res = specialJudge(baseLoc, isFirst, level)
            
            if(res) {
                sMachines[loc.toSLocation()]?.let { 
                    it.level = level
                }
            }
            
            return res
        }
        
        return false
    }

    /**
     * 多方块机器结构特判
     * 在通过一般结构判定后调用
     * 
     * @param loc 机器基准位置，一般为机器底部中心位置
     */
    open fun specialJudge(loc: Location, isFirst: Boolean, level: Short = 0): Boolean = true


    fun getOwner(sLocation: SLocation): String = sMachines[sLocation]?.owner ?: ""
    
    fun getLevel(sLocation: SLocation): Short = sMachines[sLocation]?.level ?: 0
    
    
    /**
     * 添加机器
     */
    fun addMachine(loc: Location, player: Player) {
        val sLoc = loc.toSLocation()
        allSMachines[sLoc] = this
        Shining.pluginManager.callEvent(SMachineAddEvent(this, loc, player))
    }
    
    fun addMachine(sLocation: SLocation, information: SMachineInformation = SMachineInformation()) {
        allSMachines[sLocation] = this
        sMachines[sLocation] = information
    }

    /**
     * 移除机器
     */
    fun removeMachine(loc: Location) {
        val sLoc = SLocation(loc)
        
        if(allSMachines.containsKey(sLoc)){
            val machine = allSMachines[sLoc] ?: kotlin.run { 
                allSMachines.remove(sLoc)
                return
            }
            
            if(machine.id == id) {
                allSMachines.remove(sLoc)
                Shining.pluginManager.callEvent(SMachineRemoveEvent(machine, loc))
            }
        }
    }

    /**
     * 添加配方
     */
    fun addRecipe(id: String, input: SMachineRecipe, output: SMachineRecipe, percent: Int = 100) {
        recipes[id] = SMachineRecipes(id, input, output, percent)
    }

    override fun init() {
        
    }

    /**
     * 临时数据存储
     * 
     * 增加 [block] 的 Metadata 中的整型数据直至 [maxCnt]
     * 
     * 若增加前 Metadata 中的数据:
     * 
     * · == 0 -> 设置为 1 并返回 [SMachineStatus.START]
     * 
     * · >= 1 -> 增加 [addCnt] 并返回 [SMachineStatus.RUNNING]
     * 
     * · >= [maxCnt] -> 设置为 [addCnt] 并返回 [SMachineStatus.FINISH]
     */
    protected fun addMetaCnt(block: Block, maxCnt: Int, addCnt: Int = 1): SMachineStatus {
        val meta = block.getSMetadata(wrench.plugin, id)
        var cnt = meta.asInt()
        
        val status = when {
            cnt >= maxCnt -> {
                cnt = 0
                SMachineStatus.FINISH
            }
            cnt >= 1 -> {
                cnt += addCnt
                SMachineStatus.RUNNING
            }
            else -> {
                cnt = addCnt
                SMachineStatus.START
            }
        }
        
        meta.data = cnt
        block.setMetadata(id, meta)
        return status
    }
    
    protected fun addMetaCnt(loc: Location, maxCnt: Int, addCnt: Int = 1): SMachineStatus =
        addMetaCnt(loc.block, maxCnt, addCnt)
    
    protected fun addMetaCnt(event: SMachineRunEvent, maxCnt: Int, addCnt: Int = 1): SMachineStatus =
        addMetaCnt(event.loc, maxCnt, addCnt)

    /**
     * 设置 [block] 的 Metadata 中的整型数据
     */
    protected fun setMetaCnt(block: Block, cnt: Int) {
        val meta = block.getSMetadata(wrench.plugin, id)
        meta.data = cnt
        block.setMetadata(id, meta)
    }
    
    protected fun setMetaCnt(loc: Location, cnt: Int) {
        setMetaCnt(loc.block, cnt)
    }
    
    protected fun setMetaCnt(event: SMachineRunEvent, cnt: Int) {
        setMetaCnt(event.loc, cnt)
    }


    /**
     * 持久化数据存储
     */
    fun setData(sLocation: SLocation, key: String, value: Any): Boolean {
        sMachines[sLocation]?.data?.let { information ->
            information[key] = value
            return true
        }

        return false
    }

    fun removeData(sLocation: SLocation, key: String) {
        sMachines[sLocation]?.data?.remove(key)
    }

    fun clearData(sLocation: SLocation) {
        sMachines[sLocation]?.data?.clear()
    }

    fun getData(sLocation: SLocation, key: String): Any? {
        sMachines[sLocation]?.data?.let { data ->
            if(data.containsKey(key)) {
                return data[key]
            }
        }
        return null
    }

    fun getDataOrFail(sLocation: SLocation, key: String): Any =
        getData(sLocation, key) ?: throw IllegalArgumentException("The SLocation '${toString()}' doesn't have SMachine($id) data of $key.")

    inline fun <reified T> getDataByType(sLocation: SLocation, key: String): T? {
        sMachines[sLocation]?.data?.let { data ->
            if(data.containsKey(key)) {
                data[key]?.let {
                    if(it is T) {
                        return it
                    }
                }
            }
        }
        return null
    }

    inline fun <reified T> getDataByTypeOrFail(sLocation: SLocation, key: String): T {
        getDataOrFail(sLocation, key).let {
            if(it is T) {
                return it
            }
        }

        throw IllegalArgumentException("Cannot cast data of $key to ${T::class.java.name}.")
    }



    /**
     * 在 [loc] 位置生成该机器的结构 (不会构建机器)
     */
    @ShiningTestApi
    fun buildMachine(loc: Location) {
        var theLoc: Location
        structure.structure.forEach { (coord, sBlock) -> 
            theLoc = loc.clone()
            theLoc.add(coord.x.toDouble(), coord.y.toDouble(), coord.z.toDouble())
            
            sBlock.setLocation(theLoc)
        }
    }
    
    
    companion object {
        /**
         * 所有机器的位置
         */
        private val allSMachines = HashMap<SLocation, SMachine>()


        fun Location.hasSMachine(): Boolean =
            allSMachines.containsKey(toSLocation())
        
        fun Location.getSMachine(): SMachine? {
            val sLoc = toSLocation()

            if(allSMachines.containsKey(sLoc)){
                return allSMachines[sLoc]
            }

            return null
        }
        
        fun Location.judgeSMachineStructure(player: Player, isWrench: Boolean = false): Boolean {
            val sLoc = SLocation(this)

            allSMachines[sLoc]?.let { machine ->
                var level: Short = 0
                machine.sMachines[sLoc]?.let { 
                    level = it.level
                }
                
                return if(isWrench && machine.judgeStructure(this, level = (level + 1).toShort())) {
                    Shining.pluginManager.callEvent(SMachineUpgradeEvent(machine, this, player, (level + 1).toShort()))
                    
                    world?.playSound(this, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f)
                    player.sendMsg(machine.wrench.prefix, machine.wrench.msgMachineUpgrade)
                    
                    false
                } else if(machine.judgeStructure(this, level = level)) {
                    true
                } else {
                    allSMachines.remove(sLoc)
                    Shining.pluginManager.callEvent(SMachineRemoveEvent(machine, this))
                    
                    world?.playSound(this, Sound.ENTITY_ITEM_BREAK, 1f, 0.2f)
                    player.sendMsg(machine.wrench.msgDestroy)

                    false
                }
            }
            
            return false
        }
    }
    
}