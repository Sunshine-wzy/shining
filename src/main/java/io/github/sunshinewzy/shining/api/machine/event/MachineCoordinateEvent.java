package io.github.sunshinewzy.shining.api.machine.event;

import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate3D;
import io.github.sunshinewzy.shining.api.objects.position.Position3D;
import org.jetbrains.annotations.NotNull;

public abstract class MachineCoordinateEvent extends MachineActiveEvent implements MachineCancellable {
	@NotNull
	private final Position3D position;
	@NotNull
	private final Coordinate3D coordinate;
	private boolean cancelled = false;

	public MachineCoordinateEvent(@NotNull Position3D center, @NotNull Position3D position, @NotNull Coordinate3D coordinate) {
		super(center);
		this.position = position;
		this.coordinate = coordinate;
	}

	public MachineCoordinateEvent(boolean isAsync, @NotNull Position3D center, @NotNull Position3D position, @NotNull Coordinate3D coordinate) {
		super(isAsync, center);
		this.position = position;
		this.coordinate = coordinate;
	}

	/**
	 * Gets the absolute position of the block which triggers the machine event.
	 */
	@NotNull
	public Position3D getPosition() {
		return position;
	}

	/**
	 * Gets the coordinate of the block which triggers the machine event relative to the center.
	 */
	@NotNull
	public Coordinate3D getCoordinate() {
		return coordinate;
	}

	public boolean allowCancelled() {
		return true;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean value) {
		if (allowCancelled()) {
			cancelled = value;
		} else {
			throw new IllegalStateException("MachineEvent cannot be cancelled.");
		}
	}
}
