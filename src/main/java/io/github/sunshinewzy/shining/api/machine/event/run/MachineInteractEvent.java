package io.github.sunshinewzy.shining.api.machine.event.run;

import io.github.sunshinewzy.shining.api.machine.event.MachineCoordinateEvent;
import io.github.sunshinewzy.shining.api.machine.event.MachineHandlerList;
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate3D;
import io.github.sunshinewzy.shining.api.objects.position.Position3D;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An interactive block of the machine is interacted with by a player.
 */
public class MachineInteractEvent extends MachineCoordinateEvent {
	@NotNull
	private final PlayerInteractEvent playerInteractEvent;

	public MachineInteractEvent(@NotNull Position3D center, @NotNull Position3D position, @NotNull Coordinate3D coordinate, @NotNull PlayerInteractEvent playerInteractEvent) {
		super(center, position, coordinate);
		this.playerInteractEvent = playerInteractEvent;
	}

	@NotNull
	public PlayerInteractEvent getPlayerInteractEvent() {
		return playerInteractEvent;
	}

	@NotNull
	@Override
	public MachineHandlerList getHandlers() {
		return handlers;
	}
	
	public static MachineHandlerList getHandlerList() {
		return handlers;
	}

	public static final MachineHandlerList handlers = new MachineHandlerList();
}
