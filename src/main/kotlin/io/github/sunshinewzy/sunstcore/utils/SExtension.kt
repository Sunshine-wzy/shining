package io.github.sunshinewzy.sunstcore.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.core.data.legacy.internal.SunSTPlayerData
import io.github.sunshinewzy.sunstcore.core.task.TaskBase
import io.github.sunshinewzy.sunstcore.core.task.TaskProgress
import io.github.sunshinewzy.sunstcore.core.task.TaskProject
import io.github.sunshinewzy.sunstcore.core.task.TaskStage
import io.github.sunshinewzy.sunstcore.interfaces.Itemable
import io.github.sunshinewzy.sunstcore.interfaces.Materialsable
import io.github.sunshinewzy.sunstcore.listeners.BlockListener
import io.github.sunshinewzy.sunstcore.objects.*
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.isItemSimilar
import io.github.sunshinewzy.sunstcore.utils.SReflect.damage
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockFace.*
import org.bukkit.block.Chest
import org.bukkit.block.Hopper
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.*
import org.bukkit.material.MaterialData
import org.bukkit.metadata.MetadataValueAdapter
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.BoundingBox
import taboolib.common.platform.ProxyCommandSender
import taboolib.module.chat.colored
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.math.min
import kotlin.random.Random


//region Any 对象

inline fun <reified T> Any.castList(): ArrayList<T> {
    val list = ArrayList<T>()
    if(this is List<*>){
        forEach { 
            if(it is T)
                list += it
        }
    }
    return list
}

/**
 * Object转Map
 */
inline fun <reified K, reified V> Any.castMap(kClazz: Class<K>, vClazz: Class<V>): HashMap<K, V>? {
    val result = HashMap<K, V>()
    if (this is Map<*, *>) {
        for ((key, value) in this) {
            if(key != null && value != null && key is K && value is V){
                result[kClazz.cast(key)] = vClazz.cast(value)
            }
        }
        return result
    }
    return null
}

inline fun <reified K, reified V> Any.castMap(kClazz: Class<K>, vClazz: Class<V>, targetMap: MutableMap<K, V>): Boolean {
    if (this is Map<*, *>) {
        for ((key, value) in this) {
            if(key != null && value != null && key is K && value is V)
                targetMap[kClazz.cast(key)] = vClazz.cast(value)
        }
        return true
    }
    return false
}

inline fun <reified K, reified V> Any.castMap(targetMap: MutableMap<K, V>): Boolean {
    if(castMap(K::class.java, V::class.java, targetMap))
        return true
    return false
}

fun Any.castMapBoolean(): HashMap<String, Boolean> {
    val map = HashMap<String, Boolean>()
    
    if(this is Map<*, *>){
        forEach { key, value -> 
            if(key != null && value != null && key is String && value is Boolean){
                map[key] = value
            }
        }
    }
    
    return map
}

fun Any.castMapString(): HashMap<String, String> {
    val map = HashMap<String, String>()

    if(this is Map<*, *>){
        forEach { key, value ->
            if(key is String && value is String){
                map[key] = value
            }
        }
    }

    return map
}

//endregion

//region TaskModule 任务模块

fun Player.hasCompleteTask(task: TaskBase?): Boolean {
    if(task == null) return true
    
    val taskProject = task.taskStage.taskProject
    val progress = taskProject.getProgress(this)
    
    return progress.hasCompleteTask(task)
}

fun Player.hasCompleteStage(stage: TaskStage?): Boolean {
    if(stage == null) return true
    if(stage.finalTask == null) return true
    
    val progress = stage.taskProject.getProgress(this)
    return progress.hasCompleteStage(stage)
}

//endregion

//region Player 玩家

fun Player.openInvWithSound(inv: Inventory, openSound: Sound, volume: Float, pitch: Float) {
    playSound(location, openSound, volume, pitch)
    openInventory(inv)
}

/**
 * 给予玩家物品时检测玩家背包是否已满
 * 如果未满则直接添加到玩家背包
 * 否则以掉落物的形式生成到玩家附近
 */
fun Player.giveItem(item: ItemStack, amount: Int = 0) {
    if(amount > 0) {
        if(amount < 64) {
            item.amount = amount
        } else item.amount = 64
    }
    
    if(inventory.isFull()) {
        world.dropItem(location, item)
    } else inventory.addItem(item)
}

fun Player.giveItem(items: Array<ItemStack>) {
    items.forEach { 
        giveItem(it)
    }
}

fun Player.giveItem(items: List<ItemStack>) {
    items.forEach { 
        giveItem(it)
    }
}

fun Player.giveItem(item: Itemable, amount: Int = 0) {
    giveItem(item.getSItem(), amount)
}

fun Player.giveItemInMainHand(item: ItemStack) {
    if(inventory.itemInMainHand.type == Material.AIR) {
        inventory.setItemInMainHand(item)
        return
    }
    
    giveItem(item)
}

fun Player.giveItemInMainHand(item: Itemable) {
    giveItemInMainHand(item.getSItem())
}

/**
 * 获取 [taskProject] 的任务进度
 */
fun Player.getProgress(taskProject: TaskProject): TaskProgress = taskProject.getProgress(this)

/**
 * 获取 [task] 所在 [TaskProject] 的任务进度
 */
fun Player.getProgress(task: TaskBase): TaskProgress = getProgress(task.taskStage.taskProject)

/**
 * 完成某项任务
 * @param task 任务
 */
fun Player.completeTask(task: TaskBase, isCompleted: Boolean = true) {
    val progress = getProgress(task)
    progress.completeTask(task, isCompleted)
}


/**
 * 生成粒子效果
 */
fun Player.spawnParticle(particle: Particle, listLoc: List<Location>, count: Int) {
    listLoc.forEach {
        spawnParticle(particle, it, count)
    }
}

fun Player.spawnParticle(particle: Particle, listLoc: List<Location>, count: Int, offsetX: Double, offsetY: Double, offsetZ: Double) {
    listLoc.forEach {
        spawnParticle(particle, it, count, offsetX, offsetY, offsetZ)
    }
}

/**
 * 在玩家权限允许的范围内尝试放置方块
 * @param loc 放置方块的位置
 * @param clickedBlock 玩家点击的方块
 * @param item 尝试放置的物品
 * @param block 判断是否放置方块的代码块
 */
fun Player.tryToPlaceBlock(loc: Location, clickedBlock: Block, item: ItemStack, block: BlockPlaceEvent.() -> Boolean) {
    val event = BlockPlaceEvent(
        loc.block,
        loc.block.state,
        clickedBlock,
        item,
        this,
        SReflect.canBuild(world, this, loc.blockX, loc.blockZ),
        EquipmentSlot.HAND
    )
    BlockListener.tryToPlaceBlockLocations[loc.clone()] = block
    SunSTCore.pluginManager.callEvent(event)
}

fun Player.damageItemInMainHand(damage: Int = 1) {
    inventory.setItemInMainHand(inventory.itemInMainHand.damage(damage, this)) 
}

fun Player.damageItemInOffHand(damage: Int = 1) {
    inventory.setItemInOffHand(inventory.itemInOffHand.damage(damage, this))
}

fun Player.openInventoryWithSound(
    inv: Inventory,
    sound: Sound = Sound.ENTITY_HORSE_ARMOR,
    volume: Float = 1f,
    pitch: Float = 1.2f
) {
    playSound(location, sound, volume, pitch)
    openInventory(inv)
}

fun Player.addData(key: String, value: String) {
    SunSTPlayerData.addData(this, key, value)
}

fun Player.removeData(key: String) {
    SunSTPlayerData.removeData(this, key)
}

fun Player.getData(key: String): String? =
    SunSTPlayerData.getData(this, key)

fun Player.getDataOrFail(key: String): String =
    SunSTPlayerData.getDataOrFail(this, key)

fun UUID.findPlayer(): Player? =
    Bukkit.getPlayer(this)

//endregion

//region Inventory 物品栏

/**
 * 判断物品栏中是否含有 [amount] 数量的物品 [item]
 */
fun Inventory.containsItem(item: ItemStack, amount: Int = 1): Boolean {
    if(amount <= 0) return true
    
    val theItem = item.clone()
    var cnt = theItem.amount * amount
    theItem.amount = 1
    
    storageContents.forEach {
        if(it == null) return@forEach
        
        if (it.isItemSimilar(theItem)) {
            cnt -= it.amount
            if (cnt <= 0) return true
        }
    }
    
    return false
}

fun Inventory.containsItem(items: Array<ItemStack>): Boolean {
    items.forEach { 
        if(!containsItem(it)) return false
    }
    return true
}

fun Inventory.containsItem(types: List<Material>, amount: Int = 1): Boolean {
    if(amount <= 0) return true
    var cnt = amount

    storageContents.forEach {
        if(it == null) return@forEach

        if (it.type in types) {
            cnt -= it.amount
            if (cnt <= 0) return true
        }
    }

    return false
}

fun Inventory.containsItem(types: Materialsable, amount: Int = 1): Boolean = containsItem(types.types(), amount)

/**
 * 移除物品栏中 [amount] 数量的物品 [item]
 */
fun Inventory.removeSItem(item: ItemStack, amount: Int = 1): Boolean {
    if(amount <= 0) return true

    val theItem = item.clone()
    var cnt = theItem.amount * amount
    theItem.amount = 1
    
    storageContents.forEach {
        if(it == null) return@forEach

        if (it.isItemSimilar(theItem)) {
            val theCnt = cnt
            cnt -= it.amount

            if(it.amount > theCnt) it.amount -= theCnt
            else it.amount = 0
            
            if (cnt <= 0) return true
        }
    }

    return false
}

fun Inventory.removeSItem(items: Array<ItemStack>): Boolean {
    items.forEach { 
        if(!removeSItem(it)) return false
    }
    return true
}

fun Inventory.removeSItem(items: List<ItemStack>): Boolean {
    items.forEach {
        if(!removeSItem(it)) return false
    }
    return true
}

fun Inventory.removeSItem(type: Material, amount: Int = 1): Boolean {
    if(amount <= 0) return true
    var cnt = amount

    storageContents.forEach {
        if(it == null) return@forEach

        if (it.type == type) {
            val theCnt = cnt
            cnt -= it.amount

            if(it.amount > theCnt) it.amount -= theCnt
            else it.amount = 0

            if (cnt <= 0) return true
        }
    }

    return false
}

fun Inventory.removeSItem(types: List<Material>, amount: Int = 1): Boolean {
    if(amount <= 0) return true
    var cnt = amount

    storageContents.forEach {
        if(it == null) return@forEach

        if (it.type in types) {
            val theCnt = cnt
            cnt -= it.amount

            if(it.amount > theCnt) it.amount -= theCnt
            else it.amount = 0

            if (cnt <= 0) return true
        }
    }

    return false
}

fun Inventory.removeSItem(types: Materialsable, amount: Int = 1): Boolean = removeSItem(types.types(), amount)


fun PlayerInventory.removeHandItem(item: ItemStack, amount: Int = 1): Boolean {
    if(amount <= 0) return true
    val handItem = itemInMainHand
    if(handItem.type == Material.AIR) return false

    val theItem = item.clone()
    val cnt = theItem.amount * amount
    theItem.amount = 1
    
    if(handItem.isItemSimilar(theItem)) {
        return if(handItem.amount > cnt) {
            handItem.amount -= cnt
            true
        } else {
            handItem.amount = 0
            false
        }
    }
    
    return false
}

fun PlayerInventory.removeOffHandItem(item: ItemStack, amount: Int = 1): Boolean {
    if(amount <= 0) return true
    val handItem = itemInOffHand
    if(handItem.type == Material.AIR) return false

    val theItem = item.clone()
    val cnt = theItem.amount * amount
    theItem.amount = 1

    if(handItem.isItemSimilar(theItem)) {
        return if(handItem.amount > cnt) {
            handItem.amount -= cnt
            true
        } else {
            handItem.amount = 0
            false
        }
    }

    return false
}


fun Inventory.isFull(): Boolean = firstEmpty() == -1 || firstEmpty() > size

fun Inventory.setItem(order: Int, item: Itemable) {
    setItem(order, item.getSItem())
}

fun Inventory.setItem(x: Int, y: Int, item: ItemStack) {
    setItem(x orderWith y, item)
}

fun Inventory.setItem(x: Int, y: Int, item: Itemable) {
    setItem(x orderWith y, item.getSItem())
}

fun Inventory.setItems(start: Int, end: Int, width: Int, items: List<ItemStack>): ArrayList<ItemStack> {
    var j = 0
    val list = arrayListOf<ItemStack>()
    
    for(ptr in start..end step 9) {
        for(i in ptr until ptr + width) {
            if(j in items.indices)
                setItem(i, items[j])
            else return list

            j++
        }
    }
    
    for(k in j until items.size) {
        list += items[k]
    }
    return list
}

fun Inventory.setItems(startX: Int, startY: Int, endX: Int, endY: Int, width: Int, items: List<ItemStack>): ArrayList<ItemStack> =
    setItems(startX orderWith startY, endX orderWith endY, width, items)

fun <T> Inventory.actionList(start: Int, end: Int, width: Int, list: List<T>, action: T.(Int) -> Unit): ArrayList<T> {
    var j = 0
    val resList = arrayListOf<T>()

    for(ptr in start..end step 9) {
        for(i in ptr until ptr + width) {
            if(j in list.indices)
                action(list[j], i)
            else return resList

            j++
        }
    }

    for(k in j until list.size) {
        resList += list[k]
    }
    return resList
}

fun <T> Inventory.actionList(startX: Int, startY: Int, endX: Int, endY: Int, width: Int, list: List<T>, action: T.(Int) -> Unit): ArrayList<T> =
    actionList(startX orderWith startY, endX orderWith endY, width, list, action)

/**
 * @param action First Int is page, second Int is order.
 */
fun <T> Inventory.actionList(page: Int, start: Int, end: Int, width: Int, list: List<T>, action: T.(Int, Int) -> Unit): ArrayList<T> {
    var j = 0
    val resList = arrayListOf<T>()

    for(ptr in start..end step 9) {
        for(i in ptr until min(ptr + width, end)) {
            if(j in list.indices)
                action(list[j], page, i)
            else return resList

            j++
        }
    }

    for(k in j until list.size) {
        resList += list[k]
    }
    return resList
}

fun <T> Inventory.actionList(page: Int, startX: Int, startY: Int, endX: Int, endY: Int, width: Int, list: List<T>, action: T.(Int, Int) -> Unit): ArrayList<T> =
    actionList(page, startX orderWith startY, endX orderWith endY, width, list, action)


/**
 * 快速创建 5*9 边框
 */
fun Inventory.createEdge(invSize: Int, edgeItem: ItemStack) {
    val meta = (if (edgeItem.hasItemMeta()) edgeItem.itemMeta else Bukkit.getItemFactory().getItemMeta(edgeItem.type)) ?: return
    meta.setDisplayName(" ")
    edgeItem.itemMeta = meta

    for(i in 0..8) {
        setItem(i, edgeItem)
        setItem(i + 9 * (invSize - 1), edgeItem)
    }
    for(i in 9..9*(invSize - 2) step 9) {
        setItem(i, edgeItem)
        setItem(i + 8, edgeItem)
    }
}

fun Inventory.setCraftSlotItem(craftOrder: Int, item: ItemStack, baseX: Int = 0, baseY: Int = 1) {
    setItem(baseX + craftOrder.toX(3), baseY + craftOrder.toY(3), item)
}

fun Inventory.setCraftSlotItem(items: Array<ItemStack>, baseX: Int = 0, baseY: Int = 1) {
    items.forEachIndexed {
            i, itemStack ->
        setCraftSlotItem(i, itemStack, baseX, baseY)
    }
}

fun Inventory.setCraftSlotItem(items: List<ItemStack>, baseX: Int = 0, baseY: Int = 1) {
    items.forEachIndexed {
            i, itemStack ->
        setCraftSlotItem(i, itemStack, baseX, baseY)
    }
}

fun Inventory.clearCraftSlotItem(baseX: Int = 0, baseY: Int = 1) {
    for(i in 0..8)
        setCraftSlotItem(i, ItemStack(Material.AIR), baseX, baseY)
}

fun Inventory.getRectangleItems(x: Int, y: Int, width: Int, height: Int): Array<ItemStack> {
    val items = Array(width * height) { ItemStack(Material.AIR) }
    
    var ptr = 0
    for(j in y until (y + height)) {
        for(i in x until (x + width)) {
            getItem(i orderWith j)?.let { 
                items[ptr] = it
            }
            ptr++
        }
    }
    
    return items
}

fun Inventory.getSquareItems(x: Int, y: Int, length: Int): Array<ItemStack> =
    getRectangleItems(x, y, length, length)

fun Inventory.removeRectangleItems(items: Array<ItemStack>, x: Int, y: Int, width: Int, height: Int): Boolean {
    val target = getRectangleItems(x, y, width, height)
    if(target.size != items.size) return false
    
    for(i in items.indices) {
        if(!target[i].isItemSimilar(items[i]))
            return false
    }
    
    for(i in items.indices) {
        target[i].amount -= items[i].amount
    }
    return true
}

fun Inventory.removeSquareItems(items: Array<ItemStack>, x: Int, y: Int, length: Int): Boolean =
    removeRectangleItems(items, x, y, length, length)


fun InventoryView.asPlayer(): Player = player as Player

//endregion

//region File 文件

fun File.getDataPath(plugin: JavaPlugin) 
    = absolutePath.split("(?<=${plugin.dataFolder.absolutePath.replace('\\', '/')}/)[\\s\\S]*(?=/$name)".toRegex()).last()

//endregion

//region Recipe 配方

fun ShapedRecipe.getRecipe(): Array<ItemStack> {
    val recipe = Array(9) { ItemStack(Material.AIR) }
    val rows = shape
    val ingredients = ingredientMap
    
    for(i in rows.indices){
        val base = i * 3
        val str = rows[i]
        
        for(j in str.indices){
            if(str[j] != ' '){
                val item = ingredients[str[j]] ?: continue
                recipe[base + j] = ItemStack(item.type)
            }
        }
    }
    
    return recipe
}

//endregion

//region Block 方块

fun Block.getDurability() = state.data.toItemStack(1).durability

fun Block.getFaceLocation(face: BlockFace): Location = location.getFaceLocation(face)

fun Block.getSMetadata(plugin: JavaPlugin, key: String): SMetadataValue {
    var meta = SMetadataValue(plugin, 0)
    if(hasMetadata(key)) {
        for(metadata in getMetadata(key)){
            if(metadata is SMetadataValue){
                meta = metadata
                break
            }
        }
    }
    return meta
}

fun Block.getSMetadataInt(plugin: JavaPlugin, key: String): Int = getSMetadata(plugin, key).asInt()


fun BlockFace.transform(): MutableList<BlockFace> =
    when(this) {
        NORTH, SOUTH -> arrayListOf(EAST, WEST, UP, DOWN)
        EAST, WEST -> arrayListOf(NORTH, SOUTH, UP, DOWN)
        UP, DOWN -> arrayListOf(NORTH, SOUTH, EAST, WEST)
        else -> arrayListOf()
    }

fun BlockFace.transform(excludeFace: BlockFace): MutableList<BlockFace> {
    val list = transform()
    list.remove(excludeFace)
    list.remove(excludeFace.oppositeFace)
    return list
}

fun Block.getChest(): Chest? {
    if(type == Material.CHEST) {
        val state = state
        if(state is Chest) return state
    }
    return null
}

fun Block.getHopper(): Hopper? {
    if(type == Material.HOPPER) {
        val state = state
        if(state is Hopper) return state
    }
    return null
}

//endregion

//region Location 位置

fun Location.getFaceLocation(face: BlockFace): Location = clone().add(face.modX.toDouble(), face.modY.toDouble(), face.modZ.toDouble())


fun Location.add(flatCoord: SFlatCoord, face: BlockFace): Location =
    when(face) {
        EAST -> add(flatCoord.x.toDouble(), flatCoord.y.toDouble(), 0.0)
        WEST -> subtract(flatCoord.x.toDouble(), flatCoord.y.toDouble(), 0.0)
        SOUTH -> add(0.0, flatCoord.y.toDouble(), flatCoord.x.toDouble())
        NORTH -> subtract(0.0, flatCoord.y.toDouble(), flatCoord.x.toDouble())
        else -> this
    }

fun Location.addClone(x: Int, y: Int, z: Int): Location =
    clone().add(x.toDouble(), y.toDouble(), z.toDouble())

fun Location.addClone(coord: Triple<Int, Int, Int>): Location =
    clone().add(coord.first.toDouble(), coord.second.toDouble(), coord.third.toDouble())

fun Location.addClone(coord: SPosition): Location =
    clone().add(coord.x.toDouble(), coord.y.toDouble(), coord.z.toDouble())

fun Location.addClone(y: Int): Location =
    addClone(0, y, 0)

fun Location.addClone(flatCoord: SFlatCoord, face: BlockFace): Location =
    clone().add(flatCoord, face)


fun Location.subtractClone(x: Int, y: Int, z: Int): Location =
    clone().add(-x.toDouble(), -y.toDouble(), -z.toDouble())

fun Location.subtractClone(coord: Triple<Int, Int, Int>): Location =
    clone().add(-coord.first.toDouble(), -coord.second.toDouble(), -coord.third.toDouble())

fun Location.subtractClone(coord: SPosition): Location =
    clone().add(-coord.x.toDouble(), -coord.y.toDouble(), -coord.z.toDouble())

fun Location.subtractClone(y: Int): Location =
    subtractClone(0, y, 0)


fun Location.judgePlaneAround(type: Material, includeCorners: Boolean = false): Boolean {
    val loc = clone()
    
    loc.x = x + 1
    if(loc.block.type != type) return false

    loc.x = x - 1
    if(loc.block.type != type) return false
    loc.x = x

    loc.z = z + 1
    if(loc.block.type != type) return false

    loc.z = z - 1
    if(loc.block.type != type) return false
    loc.z = z
    
    if(includeCorners){
        loc.x = x + 1
        
        loc.z = z + 1
        if(loc.block.type != type) return false
        
        loc.z = z - 1
        if(loc.block.type != type) return false
        
        
        loc.x = x - 1

        loc.z = z + 1
        if(loc.block.type != type) return false

        loc.z = z - 1
        if(loc.block.type != type) return false

        loc.x = x
        loc.z = z
    }
    
    return true
}

fun Location.judgePlaneAround(types: List<Material>, includeCorners: Boolean = false): Boolean {
    val loc = clone()

    loc.x = x + 1
    if(!types.contains(loc.block.type)) return false

    loc.x = x - 1
    if(!types.contains(loc.block.type)) return false
    loc.x = x

    loc.z = z + 1
    if(!types.contains(loc.block.type)) return false

    loc.z = z - 1
    if(!types.contains(loc.block.type)) return false
    loc.z = z

    if(includeCorners){
        loc.x = x + 1

        loc.z = z + 1
        if(!types.contains(loc.block.type)) return false

        loc.z = z - 1
        if(!types.contains(loc.block.type)) return false


        loc.x = x - 1

        loc.z = z + 1
        if(!types.contains(loc.block.type)) return false

        loc.z = z - 1
        if(!types.contains(loc.block.type)) return false

        loc.x = x
        loc.z = z
    }

    return true
}

fun Location.judgePlaneAround(type: Material, includeCorners: Boolean = false, judge: Block.() -> Boolean): Boolean {
    val loc = clone()

    loc.x = x + 1
    loc.block.let { if(it.type != type || !judge(it)) return false }

    loc.x = x - 1
    loc.block.let { if(it.type != type || !judge(it)) return false }
    loc.x = x

    loc.z = z + 1
    loc.block.let { if(it.type != type || !judge(it)) return false }

    loc.z = z - 1
    loc.block.let { if(it.type != type || !judge(it)) return false }
    loc.z = z

    if(includeCorners){
        loc.x = x + 1

        loc.z = z + 1
        loc.block.let { if(it.type != type || !judge(it)) return false }

        loc.z = z - 1
        loc.block.let { if(it.type != type || !judge(it)) return false }


        loc.x = x - 1

        loc.z = z + 1
        loc.block.let { if(it.type != type || !judge(it)) return false }

        loc.z = z - 1
        loc.block.let { if(it.type != type || !judge(it)) return false }

        loc.x = x
        loc.z = z
    }

    return true
}

fun Location.countPlaneAround(type: Material, includeCorners: Boolean = false): Int {
    val loc = clone()
    var cnt = 0

    loc.x = x + 1
    if(loc.block.type == type) cnt++

    loc.x = x - 1
    if(loc.block.type == type) cnt++
    loc.x = x

    loc.z = z + 1
    if(loc.block.type == type) cnt++

    loc.z = z - 1
    if(loc.block.type == type) cnt++
    loc.z = z

    if(includeCorners){
        loc.x = x + 1

        loc.z = z + 1
        if(loc.block.type == type) cnt++

        loc.z = z - 1
        if(loc.block.type == type) cnt++


        loc.x = x - 1

        loc.z = z + 1
        if(loc.block.type == type) cnt++

        loc.z = z - 1
        if(loc.block.type == type) cnt++

        loc.x = x
        loc.z = z
    }

    return cnt
}

fun Location.countPlaneAround(type: Material, includeCorners: Boolean = false, judge: Block.() -> Boolean): Int {
    val loc = clone()
    var cnt = 0

    loc.x = x + 1
    loc.block.let { if(it.type == type && judge(it)) cnt++ }

    loc.x = x - 1
    loc.block.let { if(it.type == type && judge(it)) cnt++ }
    loc.x = x

    loc.z = z + 1
    loc.block.let { if(it.type == type && judge(it)) cnt++ }

    loc.z = z - 1
    loc.block.let { if(it.type == type && judge(it)) cnt++ }
    loc.z = z

    if(includeCorners){
        loc.x = x + 1

        loc.z = z + 1
        loc.block.let { if(it.type == type && judge(it)) cnt++ }

        loc.z = z - 1
        loc.block.let { if(it.type == type && judge(it)) cnt++ }


        loc.x = x - 1

        loc.z = z + 1
        loc.block.let { if(it.type == type && judge(it)) cnt++ }

        loc.z = z - 1
        loc.block.let { if(it.type == type && judge(it)) cnt++ }

        loc.x = x
        loc.z = z
    }

    return cnt
}

operator fun Pair<Location, Location>.contains(location: Location) =
    location.toVector() in BoundingBox.of(first, second)

//endregion

//region Material 材料

fun MaterialData.getDurability(): Short = toItemStack(1).durability

//endregion

//region Metadata 自定义元数据

inline fun <reified F, reified S> MetadataValueAdapter.asPair(default: Pair<F, S>): Pair<F, S> {
    val obj = value()
    if(obj is Pair<*, *>){
        val (first, second) = obj
        if(first is F && second is S){
            return first to second
        }
    }
    
    return default
}

//endregion

//region Random 随机数

fun Random.Default.getInt(st: Int, ed: Int): Int = nextInt(st, ed + 1)

fun Random.Default.getInt(ed: Int): Int = getInt(1, ed)

fun Random.Default.isInPercent(percent: Int): Boolean = getInt(100) in 1..percent

//endregion

//region CommandSender

/**
 * 发送消息 (使用 '&' 作为颜色符号)
 */
fun CommandSender.sendMsg(msg: String) {
    sendMessage(msg.replace('&', '§'))
}

fun CommandSender.sendMsg(prefix: String, msg: String) {
    sendMessage("&f[$prefix&f] ".replace('&', '§') + msg.replace('&', '§'))
}

fun ProxyCommandSender.sendMsg(prefix: String, msg: List<String>) {
    msg.forEach { 
        sendMessage("&f[$prefix&f] $it".colored())
    }
}

//endregion

//region Collection

fun <E> Collection<E>.toLinkedList(): LinkedList<E> {
    val list = LinkedList<E>()
    list.addAll(this)
    return list
}

fun <E> LinkedList<E>.copy(): LinkedList<E> {
    val list = LinkedList<E>()
    list.addAll(this)
    return list
}

fun List<ItemStack>.firstOrAir(): ItemStack = firstOrNull() ?: SItem(Material.AIR)

fun List<ItemStack>.cloneFirstOrAir(): ItemStack = firstOrNull()?.clone() ?: SItem(Material.AIR)

//endregion

//region Array

fun <E> Array<out E>.toLinkedList(): LinkedList<E> {
    val list = LinkedList<E>()
    list.addAll(this)
    return list
}

fun <E> Array<out E>.toArrayList(): ArrayList<E> {
    val list = ArrayList<E>()
    list.addAll(this)
    return list
}

fun Array<ItemStack>.typeHash(): Int {
    var hash = 1
    forEach { 
        hash = hash * 31 + it.type.hashCode()
    }
    return hash
}

//endregion

//region Map

fun <K, T> MutableMap<K, MutableList<T>>.putElement(key: K, element: T) {
    val value = this[key]
    if(value != null) {
        value += element
    } else this[key] = arrayListOf(element)
}

fun <K, T> MutableMap<K, MutableList<T>>.clearAndPutElement(key: K, element: T) {
    val value = this[key]
    if(value != null) {
        value.clear()
        value += element
    } else this[key] = arrayListOf(element)
}

//endregion

//region Event

fun InventoryClickEvent.getPlayer(): Player = view.player as Player

//endregion

//region String

fun String.isLetterOrDigitOrChinese(): Boolean =
    matches("^[a-z\\dA-Z\u4e00-\u9fa5]+$".toRegex())

fun String.isLetterOrDigitOrUnderline(): Boolean =
    matches("^\\w+$".toRegex())

//endregion

//region Json

fun ObjectNode.putPrimitive(fieldName: String, v: Any): Boolean {
    if(v is Int) {
        put(fieldName, v)
        return true
    }

    if(v is Long) {
        put(fieldName, v)
        return true
    }

    if(v is Float) {
        put(fieldName, v)
        return true
    }

    if(v is Double) {
        put(fieldName, v)
        return true
    }

    if(v is Short) {
        put(fieldName, v)
        return true
    }

    if(v is Boolean) {
        put(fieldName, v)
        return true
    }

    if(v is String) {
        put(fieldName, v)
        return true
    }

    if(v is ByteArray) {
        put(fieldName, v)
        return true
    }

    if(v is BigDecimal) {
        put(fieldName, v)
        return true
    }

    if(v is BigInteger) {
        put(fieldName, v)
        return true
    }
    
    return false
}

fun JsonNode.asPrimitiveOrNull(): Any? {
    if(isInt) return intValue()
    if(isLong) return longValue()
    if(isFloat) return floatValue()
    if(isDouble) return doubleValue()
    if(isShort) return shortValue()
    if(isBoolean) return booleanValue()
    if(isTextual) return textValue()
    if(isBinary) return binaryValue()
    if(isBigDecimal) return decimalValue()
    if(isBigInteger) return bigIntegerValue()
    
    return null
}

fun JsonGenerator.writePrimitiveField(fieldName: String, v: Any): Boolean {
    if(v is Int) {
        writeNumberField(fieldName, v)
        return true
    }

    if(v is Long) {
        writeNumberField(fieldName, v)
        return true
    }

    if(v is Float) {
        writeNumberField(fieldName, v)
        return true
    }

    if(v is Double) {
        writeNumberField(fieldName, v)
        return true
    }

    if(v is Short) {
        writeNumberField(fieldName, v)
        return true
    }

    if(v is Boolean) {
        writeBooleanField(fieldName, v)
        return true
    }

    if(v is String) {
        writeStringField(fieldName, v)
        return true
    }

    if(v is ByteArray) {
        writeBinaryField(fieldName, v)
        return true
    }

    if(v is BigDecimal) {
        writeNumberField(fieldName, v)
        return true
    }

    if(v is BigInteger) {
        writeNumberField(fieldName, v)
        return true
    }
    
    return false
}

//endregion