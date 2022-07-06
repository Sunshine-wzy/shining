package io.github.sunshinewzy.sunstcore.objects

import io.github.sunshinewzy.sunstcore.modules.data.sunst.SLocationData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.NumberConversions

class SLocation {
    val world: String
    val x: Int
    val y: Int
    val z: Int
    
    
    constructor(world: String, x: Int, y: Int, z: Int) {
        this.world = world
        this.x = x
        this.y = y
        this.z = z
    }
    
    constructor(world: World, x: Int, y: Int, z: Int) : this(world.name, x, y, z)
    
    constructor(loc: Location) : this(loc.world!!, loc.blockX, loc.blockY, loc.blockZ)

    constructor(str: String) {
        val args = str.split(";")
        if(args.size == 2){
            this.world = args[0]
            
            val coord = args[1].split(",")
            if(coord.size == 3){
                this.x = NumberConversions.toInt(coord[0])
                this.y = NumberConversions.toInt(coord[1])
                this.z = NumberConversions.toInt(coord[2])
            } else throw formatException
        } else throw formatException
    }
    
    
    fun isSimilar(loc: Location): Boolean =
        world == loc.world?.name && x == loc.blockX && y == loc.blockY && z == loc.blockZ
    
    fun setSBlock(sBlock: SBlock) {
        locations[this] = sBlock
    }
    
    fun getSBlock(): SBlock {
        if (locations.containsKey(this)) {
            return locations[this] ?: kotlin.run {
                val block = getBlock() ?: return SBlock(Material.AIR)
                return SBlock(block)
            }
        } else {
            val block = getBlock() ?: return SBlock(Material.AIR)
            return SBlock(block)
        }
    }
    
    fun getBlock(): Block? {
        val world = Bukkit.getServer().getWorld(world) ?: return null
        return world.getBlockAt(x, y, z)
    }
    
    
    fun addData(key: String, value: String) {
        SLocationData.addData(world, toString(), key, value)
    }
    
    fun removeData(key: String) {
        SLocationData.removeData(world, toString(), key)
    }
    
    fun clearData() {
        SLocationData.clearData(world, toString())
    }
    
    fun getData(key: String): String? =
        SLocationData.getData(world, toString(), key)
    
    fun getDataOrFail(key: String): String =
        SLocationData.getDataOrFail(world, toString(), key)
    
    fun toLocation(): Location = Location(Bukkit.getWorld(world), x.toDouble(), y.toDouble(), z.toDouble())
    

    override fun equals(other: Any?): Boolean =
        when {
            other == null -> false
            this === other -> true
            other !is SLocation -> false
            
            else -> world == other.world && x == other.x && y == other.y && z == other.z
        }

    override fun hashCode(): Int {
        var hash = 1
        hash = hash * 31 + world.hashCode()
        hash = hash * 31 + x
        hash = hash * 31 + y
        hash = hash * 31 + z
        return hash
    }

    override fun toString(): String =
        "$world;$x,$y,$z"
    
    
    companion object {
        private val locations = HashMap<SLocation, SBlock>()
        private val formatException = IllegalArgumentException("The format of SLocation must be 'world;x,y,z'.")
        
        
        fun Location.toSLocation(): SLocation = SLocation(this)
        
        fun Block.getSLocation(): SLocation = SLocation(location)
    }
}