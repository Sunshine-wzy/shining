package io.github.sunshinewzy.shining.core.effect

import taboolib.common.util.Location
import taboolib.common.util.Vector
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.ParticleSpawner
import taboolib.module.effect.utils.VectorUtils
import kotlin.math.max
import kotlin.math.min

/**
 * 表示一个边缘立方体
 *
 * @param minLoc 一个点
 * @param maxLoc 另外一个点
 * @param step   绘制边框时的步进长度
 */
class EdgeCube @JvmOverloads constructor(
    private val minLoc: Location,
    private val maxLoc: Location,
    spawner: ParticleSpawner,
    private val step: Double = 0.2
) : ParticleObj(spawner) {

    init {
        require(minLoc.world == maxLoc.world) { "These two coordinates correspond to different worlds." }
    }

    override fun show() {
        // 获得最大最小的两个点
        val minX = min(minLoc.blockX, maxLoc.blockX) - 0.5
        val minY = min(minLoc.blockY, maxLoc.blockY) - 0.5
        val minZ = min(minLoc.blockZ, maxLoc.blockZ) - 0.5
        val maxX = max(minLoc.blockX, maxLoc.blockX) + 0.5
        val maxY = max(minLoc.blockY, maxLoc.blockY) + 0.5
        val maxZ = max(minLoc.blockZ, maxLoc.blockZ) + 0.5
        val minLoc = Location(this.minLoc.world, minX, minY, minZ)

        // 获得立方体的 长 宽 高
        val width = maxX - minX
        val height = maxY - minY
        val depth = maxZ - minZ

        // 此处的 newOrigin是底部的四个点
        var newOrigin = minLoc
        var length: Double
        // 这里直接得到向X正半轴方向的向量
        var vector = RIGHT.clone()
        for (i in 1..4) {
            length = if (i % 2 == 0) depth else width

            // 4条高
            var j = 0.0
            while (j < height) {
                spawnParticle(newOrigin.clone().add(UP.clone().multiply(j)))
                j += step
            }

            // 第n条边
            j = 0.0
            while (j < length) {
                val spawnLoc = newOrigin.clone().add(vector.clone().multiply(j))
                spawnParticle(spawnLoc)
                spawnParticle(spawnLoc.add(0.0, height, 0.0))
                j += step
            }
            // 获取结束时的坐标
            newOrigin = newOrigin.clone().add(vector.clone().multiply(length))
            vector = VectorUtils.rotateAroundAxisY(vector, 90.0)
        }
    }

    override fun calculateLocations(): List<Location> {
        TODO("Not yet implemented")
    }
    

    companion object {
        /**
         * 向上的向量
         */
        private val UP = Vector(0, 1, 0).normalize()

        /**
         * 向 X正半轴 的向量
         */
        private val RIGHT = Vector(1, 0, 0).normalize()
    }
}