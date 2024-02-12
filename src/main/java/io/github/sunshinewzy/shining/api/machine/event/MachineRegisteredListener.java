package io.github.sunshinewzy.shining.api.machine.event;

import io.github.sunshinewzy.shining.api.machine.IMachine;
import org.jetbrains.annotations.NotNull;

public class MachineRegisteredListener {
	private final MachineListener listener;
	private final MachineEventPriority priority;
	private final IMachine machine;
	private final MachineEventExecutor executor;
	private final boolean ignoreCancelled;

	public MachineRegisteredListener(@NotNull final MachineListener listener, @NotNull final MachineEventExecutor executor, @NotNull final MachineEventPriority priority, @NotNull final IMachine machine, final boolean ignoreCancelled) {
		this.listener = listener;
		this.priority = priority;
		this.machine = machine;
		this.executor = executor;
		this.ignoreCancelled = ignoreCancelled;
	}

	/**
	 * Gets the listener for this registration
	 *
	 * @return Registered MachineListener
	 */
	@NotNull
	public MachineListener getListener() {
		return listener;
	}

	/**
	 * Gets the machine for this registration
	 *
	 * @return Registered IMachine
	 */
	@NotNull
	public IMachine getMachine() {
		return machine;
	}

	/**
	 * Gets the priority for this registration
	 *
	 * @return Registered Priority
	 */
	@NotNull
	public MachineEventPriority getPriority() {
		return priority;
	}

	/**
	 * Calls the event executor
	 *
	 * @param event The event
	 * @throws MachineEventException If an event handler throws an exception.
	 */
	public void callEvent(@NotNull final MachineEvent event) throws MachineEventException {
		if (event instanceof MachineCancellable) {
			if (((MachineCancellable) event).isCancelled() && isIgnoringCancelled()) {
				return;
			}
		}
		executor.execute(listener, event);
	}

	/**
	 * Whether this listener accepts cancelled events
	 *
	 * @return True when ignoring cancelled events
	 */
	public boolean isIgnoringCancelled() {
		return ignoreCancelled;
	}
}
