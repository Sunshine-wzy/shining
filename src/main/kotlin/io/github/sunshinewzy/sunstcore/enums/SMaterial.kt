package io.github.sunshinewzy.sunstcore.enums

import io.github.sunshinewzy.sunstcore.interfaces.Materialsable
import io.github.sunshinewzy.sunstcore.objects.SCollection
import org.bukkit.Material
import org.bukkit.Material.*

enum class SMaterial(val types: List<Material>): Materialsable {
    FENCE("FENCE"),
    FENCE_WOOD(OAK_FENCE, ACACIA_FENCE, BIRCH_FENCE, DARK_OAK_FENCE, JUNGLE_FENCE, SPRUCE_FENCE),
    
    WOOD("WOOD"),
    LOG("LOG"),
    PLANKS("PLANKS"),
    
    
    SLAB("SLAB"),
    SLAB_WOOD(OAK_SLAB, JUNGLE_SLAB, ACACIA_SLAB, BIRCH_SLAB, DARK_OAK_SLAB, SPRUCE_SLAB),
    
    WOOL("WOOL"),
    
    LEAVES("LEAVES"),
    SAPLING("SAPLING"),
    
    TRAPDOOR("TRAPDOOR"),
    TRAPDOOR_WOOD(OAK_TRAPDOOR, ACACIA_TRAPDOOR, BIRCH_TRAPDOOR, DARK_OAK_TRAPDOOR, JUNGLE_TRAPDOOR, SPRUCE_TRAPDOOR)
    
    ;
    
    
    constructor(vararg types: Material) : this(types.toList())

    constructor(name: String, isEndsWith: Boolean = true) : this(SCollection.matchMaterials(name, isEndsWith))
    

    override fun types(): List<Material> = types
    
    
    operator fun contains(type: Material): Boolean = types.contains(type)
    
    
    companion object {
        private val leavesToSapling = mapOf(ACACIA_LEAVES to ACACIA_SAPLING, BIRCH_LEAVES to BIRCH_SAPLING, DARK_OAK_LEAVES to DARK_OAK_SAPLING, JUNGLE_LEAVES to JUNGLE_SAPLING, OAK_LEAVES to OAK_SAPLING, SPRUCE_LEAVES to SPRUCE_SAPLING)
        
        fun Material.getSapling(): Material =
            leavesToSapling[this] ?: OAK_SAPLING
    }
    
}