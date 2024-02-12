package io.github.sunshinewzy.shining.api.machine.event;

import java.io.Closeable;
import java.io.IOException;

public interface MachineListener extends Closeable {

	@Override
	default void close() throws IOException {
		MachineHandlerList.unregisterAll(this);
	}
	
}
