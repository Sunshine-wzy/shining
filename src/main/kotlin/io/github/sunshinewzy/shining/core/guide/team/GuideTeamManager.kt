package io.github.sunshinewzy.shining.core.guide.team

import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeamManager
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam.Companion.getGuideTeam
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam.Companion.hasGuideTeam
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam.Companion.setupGuideTeam
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.CompletableFuture

object GuideTeamManager : IGuideTeamManager {

    override fun create(captain: Player, name: String, symbol: ItemStack): CompletableFuture<IGuideTeam?> =
        ShiningDispatchers.futureIO { GuideTeam.create(captain, name, symbol) }

    override fun create(captain: UUID, name: String, symbol: ItemStack): CompletableFuture<IGuideTeam?> =
        ShiningDispatchers.futureIO { GuideTeam.create(captain, name, symbol) }

    override fun hasGuideTeam(player: Player): CompletableFuture<Boolean> =
        ShiningDispatchers.futureIO { player.hasGuideTeam() }

    override fun hasGuideTeam(uuid: UUID): CompletableFuture<Boolean> =
        ShiningDispatchers.futureIO { GuideTeam.hasGuideTeam(uuid) }

    override fun getGuideTeam(player: Player): CompletableFuture<IGuideTeam?> =
        ShiningDispatchers.futureIO { player.getGuideTeam() }

    override fun getGuideTeam(uuid: UUID): CompletableFuture<IGuideTeam?> =
        ShiningDispatchers.futureIO { GuideTeam.getGuideTeam(uuid) }

    override fun setupGuideTeam(player: Player) {
        player.setupGuideTeam()
    }
    
}