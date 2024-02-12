package io.github.sunshinewzy.shining.api.machine.event;

import io.github.sunshinewzy.shining.api.objects.position.Position3D;
import org.jetbrains.annotations.NotNull;

public abstract class MachineActiveEvent extends MachineEvent {
	@NotNull
	private final Position3D center;

	public MachineActiveEvent(@NotNull Position3D center) {
		this.center = center;
	}

	public MachineActiveEvent(boolean isAsync, @NotNull Position3D center) {
		super(isAsync);
		this.center = center;
	}

	/**
	 * Gets the absolute position of the center block of the machine
	 */
	@NotNull
	public Position3D getCenter() {
		return center;
	}
}
