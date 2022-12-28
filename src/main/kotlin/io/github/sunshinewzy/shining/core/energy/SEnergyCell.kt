package io.github.sunshinewzy.shining.core.energy

import io.github.sunshinewzy.shining.exceptions.NoEnergyUnitException
import io.github.sunshinewzy.shining.objects.SLocation
import io.github.sunshinewzy.shining.objects.SLocation.Companion.getSLocation
import io.github.sunshinewzy.shining.objects.SLocation.Companion.toSLocation
import io.github.sunshinewzy.shining.objects.legacy.SBlock
import org.bukkit.Location
import org.bukkit.block.Block


/**
 * 能量单元
 * 
 * 表示一个由能量方块互相连接形成的单元系统
 */
class SEnergyCell<EU: SEnergyUnit>(val storage: EU) {
    
    private val locations = HashSet<SLocation>()
    
    
    fun addEnergy(unit: EU) {
        storage += unit
    }
    
    fun removeEnergy(unit: EU) {
        storage -= unit
    }
    
    fun addBlock(loc: SLocation) {
        locations += loc
        entities[loc] = this
    }
    
    fun removeBlock(loc: SLocation) {
        if(locations.contains(loc))
            locations -= loc
        
        if(entities.containsKey(loc))
            entities -= loc
    }
    
    
    companion object {
        private val entities = HashMap<SLocation, SEnergyCell<out SEnergyUnit>>()
        private val energyBlocks = ArrayList<SBlock>()
        
        
        fun <EU: SEnergyUnit> Location.addEnergyCell(cell: SEnergyCell<EU>): Boolean {
            val sLoc = toSLocation()
            
            entities[sLoc] ?: run {
                entities[sLoc] = cell
            }
            
            return false
        }
        
        fun <EU: SEnergyUnit> Block.addEnergyCell(cell: SEnergyCell<EU>): Boolean =
            location.addEnergyCell(cell)
        
        fun Location.hasEnergyCell(): Boolean =
            entities.containsKey(toSLocation())
        
        fun Block.hasEnergyCell(): Boolean =
            location.hasEnergyCell()
        
        fun Location.getEnergyCell(): SEnergyCell<out SEnergyUnit>? {
            val sLoc = toSLocation()
            if(entities.containsKey(sLoc))
                return entities[sLoc]
            return null
        }
        
        fun Block.getEnergyCell(): SEnergyCell<out SEnergyUnit>? =
            location.getEnergyCell()

        fun Location.getEnergyCellOrFail(): SEnergyCell<out SEnergyUnit> =
            getEnergyCell() ?: throw NoEnergyUnitException(this)
        
        fun Block.getEnergyCellOrFail(): SEnergyCell<out SEnergyUnit> =
            location.getEnergyCellOrFail()
        
        
        fun hasEnergyBlocks(): Boolean =
            energyBlocks.isNotEmpty()
        
        fun SBlock.isEnergyBlock(): Boolean =
            energyBlocks.contains(this)
        
        fun Block.removeEnergyBlock() {
            getEnergyCell()?.removeBlock(getSLocation())
        }
        
        fun Location.removeEnergyBlock() {
            getEnergyCell()?.removeBlock(toSLocation())
        }
    }
    
}