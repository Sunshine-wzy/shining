package io.github.sunshinewzy.shining.api.machine.event.run;

import io.github.sunshinewzy.shining.api.machine.event.MachineCoordinateEvent;
import io.github.sunshinewzy.shining.api.machine.event.MachineHandlerList;
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate3D;
import io.github.sunshinewzy.shining.api.objects.position.Position3D;
import org.jetbrains.annotations.NotNull;

/**
 * The machine is triggered to run.
 * <p>
 * The player interacts with an interactive block of the machine by right click can trigger it to run,
 * which means {@link MachineInteractEvent} will be called first and then {@link MachineRunEvent} will be called.
 * <p>
 * Additionally, the machine can be triggered to run manually.
 */
public class MachineRunEvent extends MachineCoordinateEvent {
	public MachineRunEvent(@NotNull Position3D center, @NotNull Position3D position, @NotNull Coordinate3D coordinate) {
		super(center, position, coordinate);
	}

	public MachineRunEvent(boolean isAsync, @NotNull Position3D center, @NotNull Position3D position, @NotNull Coordinate3D coordinate) {
		super(isAsync, center, position, coordinate);
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
