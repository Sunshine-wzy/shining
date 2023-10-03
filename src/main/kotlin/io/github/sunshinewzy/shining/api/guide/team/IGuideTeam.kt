package io.github.sunshinewzy.shining.api.guide.team

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

interface IGuideTeam {
    
    fun joinFuture(player: Player): CompletableFuture<Boolean>
    
    fun joinFuture(uuid: UUID): CompletableFuture<Boolean>

    fun leaveFuture(player: Player): CompletableFuture<Boolean>

    fun leaveFuture(uuid: UUID): CompletableFuture<Boolean>

    fun applyFuture(player: Player): CompletableFuture<Boolean>

    fun applyFuture(uuid: UUID): CompletableFuture<Boolean>

    fun changeCaptainFuture(player: Player): CompletableFuture<Boolean>

    fun changeCaptainFuture(uuid: UUID): CompletableFuture<Boolean>

    fun changeNameFuture(name: String): CompletableFuture<Boolean>
    
    fun getTeamDataFuture(): CompletableFuture<IGuideTeamData>
    
    fun updateTeamDataFuture(): CompletableFuture<Boolean>
    
    fun approveApplicationFuture(uuid: UUID): CompletableFuture<Boolean>
    
    fun refuseApplicationFuture(uuid: UUID): CompletableFuture<Boolean>

    fun notifyCaptainApplication()

    fun getOnlinePlayers(): List<Player>

    fun welcome(uuid: UUID)

    fun openInfoMenu(player: Player)

    fun openManageMenu(player: Player)

    fun openManageApplicationMenu(player: Player)
    
}