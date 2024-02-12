package io.github.sunshinewzy.shining.api.machine.event;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;

public class CallbackMachineListener implements MachineListener, MachineEventExecutor {
	
	private final Class<? extends MachineEvent> clazz;
	private final BiConsumer<MachineEvent, MachineListener> callback;
	private final CopyOnWriteArraySet<Class<?>> ignored = new CopyOnWriteArraySet<>();

	public CallbackMachineListener(Class<? extends MachineEvent> clazz, BiConsumer<MachineEvent, MachineListener> callback) {
		this.clazz = clazz;
		this.callback = callback;
	}

	@Override
	public void execute(@NotNull MachineListener listener, @NotNull MachineEvent event) throws MachineEventException {
		if (ignored.contains(event.getClass())) return;
		
		try {
			if (!clazz.isAssignableFrom(event.getClass())) {
				ignored.add(event.getClass());
				return;
			}
			callback.accept(event, listener);
		} catch (Throwable t) {
			throw new MachineEventException(t);
		}
	}

	public Class<? extends MachineEvent> getClazz() {
		return clazz;
	}

	public BiConsumer<? extends MachineEvent, MachineListener> getCallback() {
		return callback;
	}

	public CopyOnWriteArraySet<Class<?>> getIgnored() {
		return ignored;
	}
	
}
