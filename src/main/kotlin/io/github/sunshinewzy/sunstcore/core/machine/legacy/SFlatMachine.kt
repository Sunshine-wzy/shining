package io.github.sunshinewzy.sunstcore.core.machine.legacy

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.core.data.legacy.internal.SFlatMachineData
import io.github.sunshinewzy.sunstcore.events.smachine.SFlatMachineAddEvent
import io.github.sunshinewzy.sunstcore.events.smachine.SFlatMachineRemoveEvent
import io.github.sunshinewzy.sunstcore.events.smachine.SFlatMachineUseEvent
import io.github.sunshinewzy.sunstcore.exceptions.MachineStructureException
import io.github.sunshinewzy.sunstcore.exceptions.NoIngredientException
import io.github.sunshinewzy.sunstcore.interfaces.Initable
import io.github.sunshinewzy.sunstcore.interfaces.Registrable
import io.github.sunshinewzy.sunstcore.objects.SFlatCoord
import io.github.sunshinewzy.sunstcore.objects.SLocation
import io.github.sunshinewzy.sunstcore.objects.SLocation.Companion.toSLocation
import io.github.sunshinewzy.sunstcore.objects.legacy.SBlock
import io.github.sunshinewzy.sunstcore.utils.addClone
import io.github.sunshinewzy.sunstcore.utils.castMap
import io.github.sunshinewzy.sunstcore.utils.subscribeEvent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin

abstract class SFlatMachine(
    val plugin: JavaPlugin,
    val id: String,
    val shape: String,
    val ingredients: Map<Char, SBlock>
) : Registrable {
    val flatMachines = HashMap<SLocation, SFlatMachineInformation>()
    
    val structure = FlatCoordSBlockMap()
    val verticalStructure = FlatCoordSBlockMap()
    val otherStructure = FlatCoordSBlockMap()
    
    val centerBlock = shapeStructure(shape, ingredients, structure, verticalStructure, otherStructure)


    init {
        SFlatMachineData(plugin, this)

        val typeList = centerBlockTypes[centerBlock.type]
        if(typeList != null) {
            typeList += this
        } else centerBlockTypes[centerBlock.type] = arrayListOf(this)
    }


    abstract fun onClick(sLocation: SLocation, event: PlayerInteractEvent)


    fun getOwner(sLocation: SLocation): String = flatMachines[sLocation]?.owner ?: ""

    /**
     * 数据
     */
    fun setData(sLocation: SLocation, key: String, value: Any): Boolean {
        flatMachines[sLocation]?.data?.let { information ->
            information[key] = value
            return true
        }

        return false
    }

    fun removeData(sLocation: SLocation, key: String) {
        flatMachines[sLocation]?.data?.remove(key)
    }

    fun clearData(sLocation: SLocation) {
        flatMachines[sLocation]?.data?.clear()
    }

    fun getData(sLocation: SLocation, key: String): Any? {
        flatMachines[sLocation]?.data?.let { data ->
            if(data.containsKey(key)) {
                return data[key]
            }
        }
        return null
    }

    fun getDataOrFail(sLocation: SLocation, key: String): Any =
        getData(sLocation, key) ?: throw IllegalArgumentException("The SLocation '${toString()}' doesn't have SFlatMachine($id) data of $key.")

    inline fun <reified T> getDataByType(sLocation: SLocation, key: String): T? {
        flatMachines[sLocation]?.data?.let { data ->
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
     * 必须调用该方法以注册机器
     */
    override fun register() {

    }



    companion object : Initable {
        /**
         * 所有机器的位置
         */
        private val allFlatMachines = HashMap<SLocation, SFlatMachine>()
        private val centerBlockTypes = HashMap<Material, ArrayList<SFlatMachine>>()


        override fun init() {
            subscribeEvent<PlayerInteractEvent> {
                val clickedBlock = clickedBlock ?: return@subscribeEvent

                if(action == Action.RIGHT_CLICK_BLOCK && hand == EquipmentSlot.HAND && clickedBlock.type != Material.AIR && !player.isSneaking) {
                    val loc = clickedBlock.location
                    
                    val sFlatMachine = judge(loc, player)
                    if(sFlatMachine != null) {
                        sFlatMachine.onClick(loc.toSLocation(), this)
                        SunSTCore.pluginManager.callEvent(SFlatMachineUseEvent(sFlatMachine, loc, player))
                        
                        isCancelled = true
                    }
                    
                }
            }

        }

        fun judge(loc: Location, player: Player): SFlatMachine? {
            if(loc.block.type !in centerBlockTypes.keys) return null
            
            val sLoc = loc.toSLocation()
            val machine = loc.getSFlatMachine()
            
            if(machine != null) {
                machine.flatMachines[sLoc]?.face?.let {
                    return if(judgeStructure(loc, machine, it) == BlockFace.SELF) {
                        removeMachine(loc)
                        null
                    } else machine
                }
            } else {
                centerBlockTypes[loc.block.type]?.let { list -> 
                    for(sFlatMachine in list) {
                        val face = judgeStructure(loc, sFlatMachine)
                        if(face != BlockFace.SELF) {
                            addMachine(loc, sFlatMachine, player, face)
                            
                            return sFlatMachine
                        }
                    }
                }
            }
            
            return null
        }
        
        fun judgeStructure(
            loc: Location,
            sFlatMachine: SFlatMachine,
            face: BlockFace = BlockFace.SELF
        ): BlockFace {
            var theLoc: Location
            sFlatMachine.verticalStructure.forEach { (coord, sBlock) ->
                theLoc = loc.addClone(coord.y)

                if(sBlock.type != Material.AIR && !sBlock.isSimilar(theLoc))
                    return BlockFace.SELF
            }

            if(face != BlockFace.SELF) {
                var flag = true
                
                for((coord, sBlock) in sFlatMachine.otherStructure) {
                    theLoc = loc.addClone(coord, face)

                    if(sBlock.type != Material.AIR && !sBlock.isSimilar(theLoc)) {
                        flag = false
                        break
                    }
                }

                return if(flag) face else BlockFace.SELF
            }
            
            for(theFace in listOf(BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH)) {
                var flag = true
                
                for((coord, sBlock) in sFlatMachine.otherStructure) {
                    theLoc = loc.addClone(coord, theFace)

                    if(sBlock.type != Material.AIR && !sBlock.isSimilar(theLoc)) {
                        flag = false
                        break
                    }
                }
                
                if(flag) return theFace
            }
            
            return BlockFace.SELF
        }
        
        fun shapeStructure(
            shape: String,
            ingredients: Map<Char, SBlock>,
            structure: FlatCoordSBlockMap,
            verticalStructure: FlatCoordSBlockMap,
            otherStructure: FlatCoordSBlockMap
        ): SBlock {
            val lines = shape.split("\n")
            val tempStructure = FlatCoordSBlockMap()
            var center: SFlatCoord? = null
            
            var y = -1
            for(i in lines.lastIndex downTo 0) {
                y++
                val line = lines[i]

                line.forEachIndexed forEachX@{ x, char ->
                    if(char == ' ') return@forEachX

                    val sBlock = ingredients[char] ?: throw NoIngredientException(shape, char)
                    tempStructure[SFlatCoord(x, y)] = sBlock

                    if(char == 'x') {
                        if(center == null) {
                            center = SFlatCoord(x, y)
                        } else throw MachineStructureException(shape, "The center block (marked as 'x') cannot exist more than one.")
                    }
                }
            }
            
            if(center == null) {
                throw MachineStructureException(shape, "The structure must have a center block which is marked as 'x'.")
            }

            tempStructure.forEach { (coord, sBlock) ->
                val resCoord = SFlatCoord(coord.x - center!!.x, coord.y - center!!.y)
                
                structure[resCoord] = sBlock
                if(resCoord.x == 0) {
                    verticalStructure[resCoord] = sBlock
                } else otherStructure[resCoord] = sBlock
            }
            
            return structure[SFlatCoord(0, 0)] ?: throw MachineStructureException(shape, "Cannot find the center block.")
        }


        fun addMachine(sLocation: SLocation, sFlatMachine: SFlatMachine, information: SFlatMachineInformation = SFlatMachineInformation()) {
            allFlatMachines[sLocation] = sFlatMachine
            sFlatMachine.flatMachines[sLocation] = information
        }

        fun addMachine(location: Location, sFlatMachine: SFlatMachine, player: Player, face: BlockFace) {
            addMachine(location.toSLocation(), sFlatMachine, SFlatMachineInformation(player.uniqueId.toString(), face))
            SunSTCore.pluginManager.callEvent(SFlatMachineAddEvent(sFlatMachine, location, player, face))
        }

        fun removeMachine(sLocation: SLocation): SFlatMachine? =
            allFlatMachines.remove(sLocation)?.also {
                it.flatMachines.remove(sLocation)
            }

        fun removeMachine(location: Location): Boolean {
            removeMachine(location.toSLocation())?.let { sFlatMachine ->
                SunSTCore.pluginManager.callEvent(SFlatMachineRemoveEvent(sFlatMachine, location))
                return true
            }
            return false
        }


        fun Location.getSFlatMachine(): SFlatMachine? =
            allFlatMachines[toSLocation()]

        fun Location.hasSFlatMachine(): Boolean =
            allFlatMachines.containsKey(toSLocation())
    }

}

typealias FlatCoordSBlockMap = HashMap<SFlatCoord, SBlock>


data class SFlatMachineInformation(
    var owner: String = "",
    var face: BlockFace = BlockFace.SELF,
    val data: HashMap<String, Any> = HashMap()
) : ConfigurationSerializable {

    override fun serialize(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["owner"] = owner
        map["face"] = face.name
        map["data"] = data
        return map
    }


    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any>): SFlatMachineInformation {
            val information = SFlatMachineInformation()

            map["owner"]?.let {
                if(it is String)
                    information.owner = it
            }
            
            map["face"]?.let { 
                if(it is String)
                    information.face = BlockFace.valueOf(it)
            }

            map["data"]?.castMap(information.data)

            return information
        }
    }
}