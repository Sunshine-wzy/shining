package io.github.sunshinewzy.sunstcore.core.machine.legacy

import io.github.sunshinewzy.sunstcore.exceptions.MachineStructureException
import io.github.sunshinewzy.sunstcore.exceptions.NoIngredientException
import io.github.sunshinewzy.sunstcore.objects.SCoordinate
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.getMeta
import io.github.sunshinewzy.sunstcore.objects.legacy.SBlock
import io.github.sunshinewzy.sunstcore.utils.addClone
import io.github.sunshinewzy.sunstcore.utils.setItem
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.Inventory

/**
 * 机器结构
 * 
 * @param shape 描述机器空间结构 以\n\n作为每层之间的分隔符 以\n作为每层中每行的分隔符 (推荐使用原生字符串构造)
 * @param ingredients [shape] 中每个字符指代的方块 [SBlock] (推荐使用 mapOf('x' to SBlock(Material.XXX), 'y' to SBlock(Material.XXX)) 的形式构造)
 * @param center 机器构造(和使用)的中心坐标, 请确保该坐标对应的方块不为空气
 */
abstract class SMachineStructure(
    val size: SMachineSize,
    val shape: String,
    val ingredients: Map<Char, SBlock>,
    val center: SCoordinate
) {
    protected val upgrade = ArrayList<CoordSBlockMap>()
    
    val structure = CoordSBlockMap()
    val centerBlock: SBlock
    
    
    init {
        shapeStructure(structure, shape, ingredients)

        centerBlock = if(structure.containsKey(center)){
            val theCenterBlock = structure[center] ?: throw MachineStructureException(
                shape,
                "The center is not a block"
            )

            if(theCenterBlock.type == Material.AIR) throw  MachineStructureException(
                shape,
                "The center block cannot be AIR."
            )
            
            theCenterBlock
        }
        else throw MachineStructureException(
            shape,
            "The center is not a block."
        )
        
    }

    protected abstract fun specialStructure(structure: CoordSBlockMap, x: Int, y: Int, z: Int, sBlock: SBlock)

    protected abstract fun judge(loc: Location, struct: CoordSBlockMap = structure): Boolean
    
    /**
     * 中心对称
     *
     * 自底层向上
     * 每层之间空一行
     * 每层第一行只能有一个字符
     * 其余行 为中心对称的一个角
     * 
     * """
     * a
     * 
     * a
     * ba
     * cba
     * 
     * a
     * """
     */
    class CentralSymmetry(
        size: SMachineSize,
        shape: String,
        ingredients: Map<Char, SBlock>,
        center: SCoordinate
    ) : SMachineStructure(size, shape, ingredients, center) {
        
        override fun specialStructure(structure: CoordSBlockMap, x: Int, y: Int, z: Int, sBlock: SBlock) {
            if(z == 0){
                if(x > 0) throw MachineStructureException(
                    shape,
                    "The first line of CentralSymmetry's layer is the central block, which cannot have more than one char."
                )
                
                structure[SCoordinate(0, y, 0)] = sBlock
            }
            else{
                structure[SCoordinate(x, y, z)] = sBlock
                structure[SCoordinate(-z, y, x)] = sBlock
                structure[SCoordinate(z, y, -x)] = sBlock
                structure[SCoordinate(-x, y, -z)] = sBlock
            }
        }

        override fun judge(loc: Location, struct: CoordSBlockMap): Boolean {
            var theLoc: Location
            struct.forEach { (coord, sBlock) -> 
                theLoc = loc.addClone(coord)
                
                if(sBlock.type != Material.AIR && !sBlock.isSimilar(theLoc))
                    return false
            }
            
            return true
        }
    }

    /**
     * 特定朝向
     */
    class Orientation(
        size: SMachineSize,
        shape: String,
        ingredients: Map<Char, SBlock>,
        center: SCoordinate
    ) : SMachineStructure(size, shape, ingredients, center) {
        
        override fun specialStructure(structure: CoordSBlockMap, x: Int, y: Int, z: Int, sBlock: SBlock) {
            
        }

        override fun judge(loc: Location, struct: CoordSBlockMap): Boolean {
            
            return false
        }
    }

    
    fun judgeStructure(loc: Location, level: Short = 0): Boolean {
        if(level == 0.toShort()) {
            return judge(loc)
        }
        
        val index = level - 1
        if(index in upgrade.indices) {
            return judge(loc, upgrade[index])
        }
        
        return false
    }
    
    fun displayInInventory(inv: Inventory, layer: Int, structure: CoordSBlockMap = this.structure) {
        structure.forEach { (coord, sBlock) -> 
            val (x, y, z) = coord
            if(y != layer - 1) return@forEach
            
            val item =
                if(coord == center){
                    sBlock.getItem().clone().apply {
                        addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        val meta = getMeta()
                        val list = listOf("", "§a用扳手右键敲我以构建多方块结构~")
                        if(meta.hasLore()){
                            meta.lore?.addAll(list)
                        } else meta.lore = list
                        itemMeta = meta
                    }
                }
                else sBlock.getItem()
            
            when(size) {
                SMachineSize.SIZE3 -> {
                    if(size.isCoordInSize(coord))
                        inv.setItem(invBaseX + x, invBaseY + z, item)
                }
                
                SMachineSize.SIZE5 -> {
                    if(size.isCoordInSize(coord))
                        inv.setItem(invBaseX + x, invBaseY + z, item)
                }
            }
        }
    }
    
    
    fun addUpgrade(shape: String, ingredients: Map<Char, SBlock>): SMachineStructure {
        val map = CoordSBlockMap()
        shapeStructure(map, shape, ingredients)
        
        addUpgrade(map)
        return this
    }
    
    fun addUpgrade(struct: CoordSBlockMap): SMachineStructure {
        upgrade += struct
        
        return this
    }
    
    fun hasUpgrade(level: Short = 1): Boolean {
        if(upgrade.isEmpty()) return false
        return upgrade.size >= level
    }
    
    fun getUpgrade(level: Short): CoordSBlockMap? =
        when {
            level == 0.toShort() -> structure
            hasUpgrade(level) -> upgrade[level - 1]
            else -> null
        }
    
    fun getUpgradeOrFail(level: Short): CoordSBlockMap =
        getUpgrade(level) ?: throw IllegalArgumentException("""
            The machine structure:
            ----------------------
            $structure
            ----------------------
            doesn't have the level of $level.
        """.trimIndent())
    

    fun shapeStructure(structure: CoordSBlockMap, shape: String, ingredients: Map<Char, SBlock>) {
        shape.split("\n\n").forEachIndexed forEachY@{ y, layer ->

            layer.split("\n").forEachIndexed forEachZ@{ z, line ->
                
                line.forEachIndexed forEachX@{ x, char ->
                    if(char == ' ') return@forEachX

                    val sBlock = ingredients[char] ?: throw NoIngredientException(shape, char)
                    specialStructure(structure, x, y, z, sBlock)
                }
                
            }
            
        }
    }
    
    fun copyIngredients(): HashMap<Char, SBlock> {
        val map = HashMap<Char, SBlock>()
        map += ingredients
        return map
    }

    fun copyIngredients(vararg replace: Pair<Char, SBlock>): HashMap<Char, SBlock> {
        val map = HashMap<Char, SBlock>()
        map += ingredients
        
        replace.forEach { 
            map[it.first] = it.second
        }
        
        return map
    }

    fun copyIngredients(replace: Map<Char, SBlock>): HashMap<Char, SBlock> {
        val map = HashMap<Char, SBlock>()
        map += ingredients

        replace.forEach { (ch, sBlock) ->
            map[ch] = sBlock
        }

        return map
    }
    
    
    companion object {
        const val invBaseX = 5
        const val invBaseY = 3
    }
    
}

typealias CoordSBlockMap = HashMap<SCoordinate, SBlock>

fun CoordSBlockMap.displayInInventory(inv: Inventory, page: Int, firstLayer: Boolean = true, layer: Int = 0) {
    val theY = if(firstLayer) keys.first().y else layer - 1
    if(page != 0 && page != theY + 1) return
    
    forEach { (coord, sBlock) -> 
        val (x, y, z) = coord
        if(y != theY) return@forEach

        inv.setItem(SMachineStructure.invBaseX + x, SMachineStructure.invBaseY + z, sBlock.getItem())
    }
}

fun CoordSBlockMap.put(x: Int, y:Int, z: Int, sBlock: SBlock): CoordSBlockMap {
    put(SCoordinate(x, y, z), sBlock)
    return this
}