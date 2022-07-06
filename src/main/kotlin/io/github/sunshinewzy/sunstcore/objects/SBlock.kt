package io.github.sunshinewzy.sunstcore.objects

import io.github.sunshinewzy.sunstcore.interfaces.Materialsable
import io.github.sunshinewzy.sunstcore.objects.SLocation.Companion.toSLocation
import io.github.sunshinewzy.sunstcore.utils.castList
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack

class SBlock(val type: Material, val damage: Short = -1, var name: String = "") : ConfigurationSerializable {
    
    private var item: ItemStack = SItem(type, 1)
    private val types: ArrayList<Material> by lazy { ArrayList() }
    private var hasTypes: Boolean = false
    
    
    constructor(map: Map<String, Any>) : this((map["type"] as? String)?.let { Material.valueOf(it) } ?: Material.AIR, map["damage"] as? Short ?: -1, map["name"] as? String ?: "") {
        map["item"]?.let { 
            if(it is ItemStack)
                item = it
        }

        map["types"]?.castList<Material>()?.let { 
            types += it
        }
    }
    
    constructor(type: Material, name: String) : this(type) {
        this.name = name
    }
    
    constructor(types: List<Material>) : this(types.first()) {
        hasTypes = true
        this.types += types
    }
    
    constructor(types: Materialsable) : this(types.types())
    
    constructor(vararg types: Material) : this(types.first()) {
        hasTypes = true
        this.types += types
    }
    
    constructor(loc: Location) : this(loc.block.type)
    
    constructor(block: Block) : this(block.type)
    
    constructor(blockState: BlockState) : this(blockState.type)
    
    constructor(item: ItemStack) : this(item.type)
    
    
    fun setLocation(loc: Location) {
        val block = loc.block
        block.type = type
        
        if(hasName())
            loc.toSLocation().addData("SBlockName", name)
    }
    
    fun toItem(): ItemStack = SItem(type, 1)
    
    
    fun isSimilar(material: Material): Boolean {
        if(hasTypes) {
            return material in types
        }
        
        return this == SBlock(material)
    }
    
    fun isSimilar(block: Block): Boolean {
        if(hasTypes) {
            return block.type in types
        }
        
        return this == block.toSBlock()
    }

    fun isSimilar(block: BlockState): Boolean = isSimilar(block.block)
    
    fun isSimilar(loc: Location): Boolean = isSimilar(loc.block)
    
    
    fun hasName(): Boolean = name != ""
    
    fun isNameEqual(sBlock: SBlock): Boolean = name == sBlock.name
    
    
    fun setItem(item: ItemStack): SBlock {
        this.item = item
        return this
    }
    
    fun getItem(): ItemStack = item


    
    override fun equals(other: Any?): Boolean =
        when {
            other == null -> false
            this === other -> true
            other !is SBlock -> false
            
            else -> type == other.type && (damage == (-1).toShort() || other.damage == (-1).toShort() || damage == other.damage) && name == other.name
                    && if(hasTypes && types.isNotEmpty()) {
                        if(other.hasTypes && other.types.isNotEmpty()) {
                            if(types.size == other.types.size) {
                                var flag = true
                                for(i in types.indices) {
                                    if(types[i] != other.types[i]) {
                                        flag = false
                                        break
                                    }
                                }
                                flag
                            } else false
                        } else false
                    } else true
        }

    override fun hashCode(): Int {
        var hash = 1
        hash = hash * 31 + type.hashCode()
        hash = hash * 31 + damage.hashCode()
        hash = hash * 31 + if(hasName()) name.hashCode() else 0
        
        if(hasTypes && types.isNotEmpty()) {
            types.forEach { 
                hash = hash * 31 + it.hashCode()
            }
        }
        
        return hash
    }

    override fun toString(): String {
        if(hasTypes && types.isNotEmpty()) {
            var str = "SBlock{types=["
            types.forEach { 
                str += "$it,"
            }
            if(str.last() == ',') str.trimEnd(',')
            str += "]}"
            return str
        }

        if(hasName()) return "SBlock{type=$type,damage=$damage,name=$name}"
        
        return "SBlock{type=$type,damage=$damage}"
    }

    override fun serialize(): MutableMap<String, Any> {
        val map = HashMap<String, Any>()
        
        map["type"] = type.name
        map["damage"] = damage
        map["name"] = name
        map["item"] = item
        
        if(hasTypes) {
            map["types"] = types
        }
        
        return map
    }

    companion object {
        @JvmStatic
        val AIR = SBlock(Material.AIR)
        
        
        @JvmStatic
        fun createAir(item: ItemStack): SBlock = SBlock(Material.AIR).setItem(item)
        
        fun Location.getSBlock(): SBlock = SBlock(this)
        
        fun Block.toSBlock(): SBlock = SBlock(this)
        
        fun BlockState.toSBlock(): SBlock = SBlock(this)

    }

}