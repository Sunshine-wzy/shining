package io.github.sunshinewzy.shining.api.machine.event;

import org.jetbrains.annotations.NotNull;

public class DefaultMachineEvent extends MachineEvent implements MachineCancellable {
	
	private boolean cancelled = false;
	
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
