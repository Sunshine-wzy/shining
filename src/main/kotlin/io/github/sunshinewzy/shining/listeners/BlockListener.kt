package io.github.sunshinewzy.shining.listeners

import io.github.sunshinewzy.shining.core.data.legacy.internal.SLocationData
import io.github.sunshinewzy.shining.core.energy.SEnergyCell
import io.github.sunshinewzy.shining.core.energy.SEnergyCell.Companion.getEnergyCell
import io.github.sunshinewzy.shining.core.energy.SEnergyCell.Companion.isEnergyBlock
import io.github.sunshinewzy.shining.core.energy.SEnergyCell.Companion.removeEnergyBlock
import io.github.sunshinewzy.shining.objects.SLocation.Companion.getSLocation
import io.github.sunshinewzy.shining.objects.legacy.SBlock
import io.github.sunshinewzy.shining.objects.legacy.SBlock.Companion.toSBlock
import io.github.sunshinewzy.shining.utils.BlockOperator.Companion.operate
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

object BlockListener {
    val tryToPlaceBlockLocations = HashMap<Location, BlockPlaceEvent.() -> Boolean>()


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onBlockPlace(e: BlockPlaceEvent) {
        val block = e.blockPlaced
        val loc = block.location
        val item = e.itemInHand

        if (tryToPlaceBlockLocations.isNotEmpty()) {

            if (tryToPlaceBlockLocations.containsKey(loc)) {
                if (!e.isCancelled && block.type == Material.AIR && item.type != Material.AIR && item.amount > 0) {
                    val theBlock = tryToPlaceBlockLocations[loc] ?: return

                    if (theBlock(e))
                        SBlock(item).setLocation(loc)
                }

                tryToPlaceBlockLocations.remove(loc)
            }
        }

        if (SEnergyCell.hasEnergyBlocks()) {
            val sBlock = SBlock(item)

            if (sBlock.isEnergyBlock()) {
                block.operate {
                    val flag = surroundings {
                        if (sBlock.isSimilar(this)) {
                            getEnergyCell()?.let {
                                it.addBlock(getSLocation())
                                return@surroundings true
                            }
                        }

                        false
                    }

                    if (!flag) {
//                        block.addEnergyEntity()
                    }
                }
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!e.isCancelled) {
            val block = e.block

            if (SEnergyCell.hasEnergyBlocks()) {
                val sBlock = block.toSBlock()

                if (sBlock.isEnergyBlock()) {
                    block.removeEnergyBlock()
                }
            }

            SLocationData.clearData(block.world.name, block.getSLocation().toString())
        }
    }

}