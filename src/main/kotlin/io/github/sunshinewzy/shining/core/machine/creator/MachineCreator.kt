package io.github.sunshinewzy.shining.core.machine.creator

import io.github.sunshinewzy.shining.core.machine.creator.PlayerCreateMachineContext.Status
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.utils.isClickBlock
import io.github.sunshinewzy.shining.utils.position3D
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendLang
import java.util.concurrent.ConcurrentHashMap

object MachineCreator {
    private const val PERIOD = 20L   // tick

    private val contextMap: MutableMap<Player, PlayerCreateMachineContext> = ConcurrentHashMap()


    fun create(player: Player) {
        player.openMenu<Basic>(player.asLangText("machine-creator-create-menu")) {
            rows(3)

            map(
                "",
                "",
                ""
            )

            set('a', SItem(Material.DIAMOND)) {
                select(player)
            }

            onClick(lock = true)
        }
    }

    fun select(player: Player) {
        player.sendLang("machine-creator-select")
        contextMap[player] = PlayerCreateMachineContext()

    }


    @SubscribeEvent(EventPriority.HIGHEST)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (!event.action.isClickBlock()) return

        event.clickedBlock?.let { block ->
            val context = contextMap[event.player] ?: return
            val status = context.status

            if (status == Status.SELECT_LEFT && event.action == Action.LEFT_CLICK_BLOCK) {
                context.leftPosition = block.location.position3D
                context.checkSelect()
            } else if (status == Status.SELECT_RIGHT && event.action == Action.RIGHT_CLICK_BLOCK) {
                context.rightPosition = block.location.position3D
                context.checkSelect()
            }


        }

    }

    @Awake
    fun particleDispatcher() {
        submit(period = PERIOD, delay = PERIOD) {
            contextMap.forEach { (player, context) ->
                context.playParticle(player)
            }
        }
    }


}