package io.github.sunshinewzy.shining.api.machine.event;

import org.jetbrains.annotations.NotNull;

/**
 * Interface which defines the class for event call backs to machines
 */
public interface MachineEventExecutor {
	void execute(@NotNull MachineListener listener, @NotNull MachineEvent event) throws MachineEventException;
}
