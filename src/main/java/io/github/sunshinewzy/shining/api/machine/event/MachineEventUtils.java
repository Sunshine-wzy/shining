package io.github.sunshinewzy.shining.api.machine.event;

import io.github.sunshinewzy.shining.api.ShiningAPIProvider;
import io.github.sunshinewzy.shining.api.machine.IMachine;
import org.bukkit.Bukkit;
import org.bukkit.Warning;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class MachineEventUtils {
	
	private MachineEventUtils() {}


	@NotNull
	public static Map<Class<? extends MachineEvent>, Set<MachineRegisteredListener>> createRegisteredListeners(@NotNull MachineListener listener, @NotNull final IMachine machine) {
		Map<Class<? extends MachineEvent>, Set<MachineRegisteredListener>> ret = new HashMap<>();
		Set<Method> methods;
		try {
			Method[] publicMethods = listener.getClass().getMethods();
			Method[] privateMethods = listener.getClass().getDeclaredMethods();
			methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0f);
			for (Method method : publicMethods) {
				methods.add(method);
			}
			for (Method method : privateMethods) {
				methods.add(method);
			}
		} catch (NoClassDefFoundError e) {
			Bukkit.getLogger().severe("Machine " + machine.getProperty().getId() + " has failed to register events for " + listener.getClass() + " because " + e.getMessage() + " does not exist.");
			return ret;
		}

		for (final Method method : methods) {
			final MachineEventHandler eh = method.getAnnotation(MachineEventHandler.class);
			if (eh == null) continue;
			// Do not register bridge or synthetic methods to avoid event duplication
			if (method.isBridge() || method.isSynthetic()) {
				continue;
			}
			final Class<?> checkClass;
			if (method.getParameterTypes().length != 1 || !MachineEvent.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
				ShiningAPIProvider.INSTANCE.api().getPlugin().getLogger().severe("Machine " + machine.getProperty().getId() + " attempted to register an invalid MachineEventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
				continue;
			}
			final Class<? extends MachineEvent> eventClass = checkClass.asSubclass(MachineEvent.class);
			method.setAccessible(true);
			Set<MachineRegisteredListener> eventSet = ret.computeIfAbsent(eventClass, k -> new HashSet<>());

			for (Class<?> clazz = eventClass; MachineEvent.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
				// This loop checks for extending deprecated events
				if (clazz.getAnnotation(Deprecated.class) != null) {
					Warning warning = clazz.getAnnotation(Warning.class);
					Warning.WarningState warningState = Bukkit.getWarningState();
					if (!warningState.printFor(warning)) {
						break;
					}
					ShiningAPIProvider.INSTANCE.api().getPlugin().getLogger().log(
							Level.WARNING,
							String.format(
									"Machine \"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated. \"%s\".",
									machine.getProperty().getId(),
									clazz.getName(),
									method.toGenericString(),
									(warning != null && !warning.reason().isEmpty()) ? warning.reason() : "Server performance will be affected"));
					break;
				}
			}

			MachineEventExecutor executor = new MachineEventExecutor() {
				@Override
				public void execute(@NotNull MachineListener listener, @NotNull MachineEvent event) throws MachineEventException {
					try {
						if (!eventClass.isAssignableFrom(event.getClass())) {
							return;
						}
						method.invoke(listener, event);
					} catch (InvocationTargetException ex) {
						throw new MachineEventException(ex.getCause());
					} catch (Throwable t) {
						throw new MachineEventException(t);
					}
				}
			};
			eventSet.add(new MachineRegisteredListener(listener, executor, eh.priority(), machine, eh.ignoreCancelled()));
		}
		return ret;
	}
	
}
