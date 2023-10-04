package io.github.sunshinewzy.shining.api.guide.team

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.CompletableFuture

interface IGuideTeamManager {

    /**
     * Create a team with [captain] as its captain.
     *
     * @return The newly created team. Null if [captain] is already in a team.
     */
    fun create(captain: Player, name: String, symbol: ItemStack): CompletableFuture<IGuideTeam?>

    fun create(captain: UUID, name: String, symbol: ItemStack): CompletableFuture<IGuideTeam?>

    /**
     * Check if the player is in a guide team.
     */
    fun hasGuideTeam(player: Player): CompletableFuture<Boolean>
    
    fun hasGuideTeam(uuid: UUID): CompletableFuture<Boolean>

    /**
     * Get the guide team the player is in.
     *
     * If the player is not in a guide team, it will return null.
     */
    fun getGuideTeam(player: Player): CompletableFuture<IGuideTeam?>
    
    fun getGuideTeam(uuid: UUID): CompletableFuture<IGuideTeam?>

    /**
     * Open a menu allowing the player to choose to create or join a guide team.
     */
    fun setupGuideTeam(player: Player)
    
}