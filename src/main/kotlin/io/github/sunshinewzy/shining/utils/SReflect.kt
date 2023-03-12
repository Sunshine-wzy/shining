package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.interfaces.Initable
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Method
import java.util.function.Consumer

object SReflect : Initable {
    val version = Bukkit.getServer().javaClass.getPackage().name.replace(".", ",").split(",")[3]
    val nms = "net.minecraft.server.$version"
    val obc = "org.bukkit.craftbukkit.$version"

    object RClass {
        val itemStack: Class<*> = Class.forName("$nms.ItemStack")
        val entityPlayer: Class<*> = Class.forName("$nms.EntityPlayer")
        val entityLiving: Class<*> = Class.forName("$nms.EntityLiving")
        val worldServer: Class<*> = Class.forName("$nms.WorldServer")

        val craftEventFactory: Class<*> = Class.forName("$obc.event.CraftEventFactory")
        val craftWorld: Class<*> = Class.forName("$obc.CraftWorld")
        val craftPlayer: Class<*> = Class.forName("$obc.entity.CraftPlayer")
        val craftItemStack: Class<*> = Class.forName("$obc.inventory.CraftItemStack")
    }

    object RMethod {
        val canBuild: Method = RClass.craftEventFactory.getDeclaredMethod(
            "canBuild",
            RClass.worldServer,
            Player::class.java,
            Int::class.java,
            Int::class.java
        ).apply {
            isAccessible = true
        }
        val getEntityPlayer: Method = RClass.craftPlayer.getMethod("getHandle")
        val getWorldServer: Method = RClass.craftWorld.getMethod("getHandle")
        val itemDamage: Method =
            RClass.itemStack.getMethod("damage", Int::class.java, RClass.entityLiving, Consumer::class.java)
        val itemAsNMSCopy: Method = RClass.craftItemStack.getMethod("asNMSCopy", ItemStack::class.java)
        val itemAsBukkitCopy: Method = RClass.craftItemStack.getMethod("asBukkitCopy", RClass.itemStack)
    }


    override fun init() {

    }


    fun canBuild(world: World, player: Player, x: Int, z: Int): Boolean = RMethod.canBuild.invoke(
        null,
        RMethod.getWorldServer.invoke(RClass.craftWorld.cast(world)),
        player,
        x,
        z
    ) as Boolean

    fun Player.getCraftPlayer(): Any = RClass.craftPlayer.cast(this)

    fun Player.getEntityPlayer(): Any = RMethod.getEntityPlayer.invoke(getCraftPlayer())

    fun ItemStack.asNMSCopy(): Any = RMethod.itemAsNMSCopy.invoke(null, this)

    fun ItemStack.damage(damage: Int, player: Player): ItemStack {
        val nmsItem = asNMSCopy()
        val entityPlayer = player.getEntityPlayer()
        RMethod.itemDamage.invoke(nmsItem, damage, entityPlayer, Consumer<Any> {})
        return RMethod.itemAsBukkitCopy(null, nmsItem) as ItemStack
    }

}