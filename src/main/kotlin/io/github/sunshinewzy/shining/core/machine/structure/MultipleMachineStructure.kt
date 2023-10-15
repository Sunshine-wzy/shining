package io.github.sunshinewzy.shining.core.machine.structure

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate3D
import io.github.sunshinewzy.shining.api.objects.coordinate.MutableCoordinate3D
import io.github.sunshinewzy.shining.api.universal.block.UniversalBlock
import io.github.sunshinewzy.shining.core.universal.block.VanillaUniversalBlock
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import taboolib.module.nms.MinecraftVersion
import java.util.*
import kotlin.math.max
import kotlin.math.min

@JsonTypeName("multiple")
class MultipleMachineStructure : AbstractMachineStructure() {

    var center: Int = 0
    var directionMode: Boolean = true
    var direction: BlockFace = BlockFace.SELF
    @JsonIgnore
    private val structure: MutableMap<Coordinate3D, Int> = HashMap()
    @JsonProperty
    private val ingredients: ArrayList<UniversalBlock> = ArrayList()
    

    override fun check(location: Location, direction: BlockFace?): Boolean {
        if (directionMode) {
            if (direction == null || direction !in blockFaces) return false
            
            val rotator = Rotator(this.direction, direction)
            if (!compare(ingredients[center], location.block))
                return false
            
            val centerX = location.blockX
            val centerY = location.blockY
            val centerZ = location.blockZ
            val tempLocation = location.clone()
            val rotatedIngredients = ingredients.map { ingredient ->
                (ingredient as? VanillaUniversalBlock)?.let {
                    VanillaUniversalBlock(rotator.rotate(it.data))
                } ?: ingredient
            }
            
            for ((coordinate, ingredient) in structure) {
                rotator.setLocation(coordinate.x, coordinate.z)
                tempLocation.x = (centerX + rotator.rotatedX).toDouble()
                tempLocation.y = (centerY + coordinate.y).toDouble()
                tempLocation.z = (centerZ + rotator.rotatedZ).toDouble()
                if (!compare(rotatedIngredients[ingredient], tempLocation.block))
                    return false
            }
        } else {
            
        }
        
        return true
    }
    
    fun compare(ingredient: UniversalBlock, block: Block): Boolean =
        ingredient.compare(block, strictMode, ignoreAir)
    
    fun scan(centerLocation: Location, location1: Location, location2: Location, direction: BlockFace): Boolean {
        val world = location1.world ?: return false
        if (world != location2.world) return false
        structure.clear()
        ingredients.clear()
        this.direction = direction
        
        val centerX = centerLocation.blockX
        val centerY = centerLocation.blockY
        val centerZ = centerLocation.blockZ
        val ingredientToCoordinates = HashMap<UniversalBlock, HashSet<Coordinate3D>>()
        val typeToIngredients = HashMap<Material, HashSet<UniversalBlock>>()
        
        for (x in min(location1.blockX, location2.blockX)..max(location1.blockX, location2.blockX)) {
            for (y in min(location1.blockY, location2.blockY)..max(location1.blockY, location2.blockY)) {
                for (z in min(location1.blockZ, location2.blockZ)..max(location1.blockZ, location2.blockZ)) {
                    val block = world.getBlockAt(x, y, z)
                    val type = block.type
                    if (ignoreAir) {
                        val isAir = if (MinecraftVersion.major >= MinecraftVersion.V1_15) {
                            type.isAir
                        } else type == Material.AIR
                        if (isAir) continue
                    }
                    
                    val coordinate = Coordinate3D(x - centerX, y - centerY, z - centerZ)
                    val ingredients = typeToIngredients[type]
                    if (ingredients == null) {
                        val vanillaBlock = VanillaUniversalBlock(block.blockData)
                        ingredientToCoordinates[vanillaBlock] = hashSetOf(coordinate)
                        typeToIngredients[type] = hashSetOf(vanillaBlock)
                    } else {
                        var flag = true
                        for (ingredient in ingredients) {
                            if (ingredient.compare(block, strictMode, ignoreAir)) {
                                ingredientToCoordinates
                                    .getOrPut(ingredient) { HashSet() }
                                    .add(coordinate)
                                flag = false
                                break
                            }
                        }
                        
                        if (flag) {
                            val vanillaBlock = VanillaUniversalBlock(block.blockData)
                            ingredientToCoordinates
                                .getOrPut(vanillaBlock) { HashSet() }
                                .add(coordinate)
                            typeToIngredients
                                .getOrPut(type) { HashSet() }
                                .add(vanillaBlock)
                        }
                    }
                }
            }
        }

        var flagCenter = true
        for ((ingredient, coordinates) in ingredientToCoordinates) {
            if (coordinates.contains(Coordinate3D.ORIGIN)) {
                flagCenter = false
                break
            }
        }
        if (flagCenter) return false
        
        var i = 0
        ingredientToCoordinates.forEach { (ingredient, coordinates) -> 
            ingredients += ingredient
            coordinates.forEach { coordinate -> 
                if (coordinate == Coordinate3D.ORIGIN) center = i
                else structure[coordinate] = i
            }
            i++
        }
        return true
    }
    
    fun getData(): ArrayList<String> {
        val ingredientToCoordinates = HashMap<Int, HashSet<Coordinate3D>>()
        val ingredientToCoordinateRanges = HashMap<Int, HashSet<Pair<Coordinate3D, Coordinate3D>>>()
        
        val flatStructure = HashMap<Int, HashMap<Int, HashMap<Int, Int>>>()
        fun getFlatIngredient(x: Int, y: Int, z: Int): Int {
            val mapY = flatStructure[x] ?: return -1
            val mapZ = mapY[y] ?: return -1
            return mapZ[z] ?: return -1
        }
        fun setFlatIngredient(x: Int, y: Int, z: Int, ingredient: Int) {
            val mapY = flatStructure.getOrPut(x) { HashMap() }
            val mapZ = mapY.getOrPut(y) { HashMap() }
            mapZ[z] = ingredient
        }
        
        structure.forEach { (coordinate, ingredient) ->
            setFlatIngredient(coordinate.x, coordinate.y, coordinate.z, ingredient)
        }

        val scannedCoordinates = HashSet<MutableCoordinate3D>()
        val tempCoordinate = MutableCoordinate3D()
        tailrec fun findMaxCuboid(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int, ingredient: Int): Pair<Coordinate3D, Coordinate3D> {
            var flag = true
            for (yi in y1..y2) {
                for (zi in z1..z2) {
                    if (scannedCoordinates.contains(tempCoordinate.setCoordinate(x1 - 1, yi, zi)) || getFlatIngredient(x1 - 1, yi, zi) != ingredient) {
                        flag = false
                        break
                    }
                }
            }
            if (flag) return findMaxCuboid(x1 - 1, y1, z1, x2, y2, z2, ingredient)

            flag = true
            for (yi in y1..y2) {
                for (zi in z1..z2) {
                    if (scannedCoordinates.contains(tempCoordinate.setCoordinate(x2 + 1, yi, zi)) || getFlatIngredient(x2 + 1, yi, zi) != ingredient) {
                        flag = false
                        break
                    }
                }
            }
            if (flag) return findMaxCuboid(x1, y1, z1, x2 + 1, y2, z2, ingredient)

            flag = true
            for (yi in y1..y2) {
                for (xi in x1..x2) {
                    if (scannedCoordinates.contains(tempCoordinate.setCoordinate(xi, yi, z1 - 1)) || getFlatIngredient(xi, yi, z1 - 1) != ingredient) {
                        flag = false
                        break
                    }
                }
            }
            if (flag) return findMaxCuboid(x1, y1, z1 - 1, x2, y2, z2, ingredient)

            flag = true
            for (yi in y1..y2) {
                for (xi in x1..x2) {
                    if (scannedCoordinates.contains(tempCoordinate.setCoordinate(xi, yi, z2 + 1)) || getFlatIngredient(xi, yi, z2 + 1) != ingredient) {
                        flag = false
                        break
                    }
                }
            }
            if (flag) return findMaxCuboid(x1, y1, z1, x2, y2, z2 + 1, ingredient)

            flag = true
            for (zi in z1..z2) {
                for (xi in x1..x2) {
                    if (scannedCoordinates.contains(tempCoordinate.setCoordinate(xi, y1 - 1, zi)) || getFlatIngredient(xi, y1 - 1, zi) != ingredient) {
                        flag = false
                        break
                    }
                }
            }
            if (flag) return findMaxCuboid(x1, y1 - 1, z1, x2, y2, z2, ingredient)

            flag = true
            for (zi in z1..z2) {
                for (xi in x1..x2) {
                    if (scannedCoordinates.contains(tempCoordinate.setCoordinate(xi, y2 + 1, zi)) || getFlatIngredient(xi, y2 + 1, zi) != ingredient) {
                        flag = false
                        break
                    }
                }
            }
            if (flag) return findMaxCuboid(x1, y1, z1, x2, y2 + 1, z2, ingredient)
            
            return Coordinate3D(x1, y1, z1) to Coordinate3D(x2, y2, z2)
        }
        
        scannedCoordinates += MutableCoordinate3D(0, 0, 0)
        val currentCoordinate = MutableCoordinate3D()
        structure.forEach { (coordinate, ingredient) -> 
            currentCoordinate.setCoordinate(coordinate.x, coordinate.y, coordinate.z)
            if (scannedCoordinates.contains(currentCoordinate)) return@forEach
            
            val cuboid = findMaxCuboid(coordinate.x, coordinate.y, coordinate.z, coordinate.x, coordinate.y, coordinate.z, ingredient)
            if (cuboid.first == cuboid.second) {
                ingredientToCoordinates
                    .getOrPut(ingredient) { HashSet() }
                    .add(coordinate)
                scannedCoordinates += MutableCoordinate3D(coordinate.x, coordinate.y, coordinate.z)
            } else {
                ingredientToCoordinateRanges
                    .getOrPut(ingredient) { HashSet() }
                    .add(cuboid)
                for (xi in cuboid.first.x..cuboid.second.x) {
                    for (yi in cuboid.first.y..cuboid.second.y) {
                        for (zi in cuboid.first.z..cuboid.second.z) {
                            scannedCoordinates += MutableCoordinate3D(xi, yi, zi)
                        }
                    }
                }
            }
        }
        
        
        val data = ArrayList<String>()
        for (i in ingredients.indices) {
            val builder = StringBuilder()

            ingredientToCoordinateRanges[i]?.forEach { range ->
                if (builder.isNotEmpty()) builder.append(";")
                builder.append("${range.first}:${range.second}")
            }
            ingredientToCoordinates[i]?.forEach { coordinate ->
                if (builder.isNotEmpty()) builder.append(";")
                builder.append(coordinate)
            }

            data += builder.toString()
        }
        return data
    }
    
    fun setData(data: ArrayList<String>) {
        data.forEachIndexed { ingredient, str -> 
            str.split(";").forEach { range ->
                val coordinates = range.split(":")
                if (coordinates.size == 1) {
                    val coordinate = Coordinate3D.fromString(coordinates[0]) ?: return@forEach
                    structure[coordinate] = ingredient
                } else if (coordinates.size == 2) {
                    val coordinate1 = Coordinate3D.fromString(coordinates[0]) ?: return@forEach
                    val coordinate2 = Coordinate3D.fromString(coordinates[1]) ?: return@forEach
                    for (x in coordinate1.x..coordinate2.x) {
                        for (y in coordinate1.y..coordinate2.y) {
                            for (z in coordinate1.z..coordinate2.z) {
                                if (x == coordinate1.x && y == coordinate1.y && z == coordinate1.z)
                                    structure[coordinate1] = ingredient
                                else if (x == coordinate2.x && y == coordinate2.y && z == coordinate2.z)
                                    structure[coordinate2] = ingredient
                                else structure[Coordinate3D(x, y, z)] = ingredient
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    companion object {
        val blockFaces: Set<BlockFace> = EnumSet.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)
    }
    
}